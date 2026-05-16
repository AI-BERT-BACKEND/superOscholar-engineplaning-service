package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.context.OverloadedDayRecord;
import com.aibert.dosw.domain.model.schedule.AvailabilityFilter;
import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.ScheduledBlock;
import com.aibert.dosw.domain.model.schedule.TimeSlot;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.DistributeTasksUseCase;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the Task Distribution Engine (AIB-24 / R16).
 * Distributes pending tasks into available time slots respecting
 * MAX_MINUTES_PER_DAY = 240,
 * strictly prioritizing HIGH/CRITICAL tasks and applying the AIB-22.4
 * correction factor.
 */
@Service
@RequiredArgsConstructor
public class DistributeTasksUseCaseImpl implements DistributeTasksUseCase {

    private static final int MAX_MINUTES_PER_DAY = 240; // RN-03 (AIB-24 / AIB-27)

    private final TaskProviderPort taskProviderPort;
    private final ScheduleProviderPort scheduleProviderPort;

    @Override
    public WeeklyDistributionPlan distribute(String studentId, LocalDate weekStartDate) {

        // 1. Fetch pending tasks, availability, and unavailable blocks
        List<PlanningTask> pendingTasks = taskProviderPort.getPendingTasksByUser(studentId);
        List<DailySchedule> weeklySchedules = scheduleProviderPort.getWeeklySchedule(studentId);
        List<UnavailableBlock> unavailableBlocks = scheduleProviderPort.getUnavailableBlocks(studentId);

        // FA-02: No pending tasks (AIB-24)
        if (pendingTasks == null || pendingTasks.isEmpty()) {
            return WeeklyDistributionPlan.builder()
                    .studentId(studentId)
                    .assignedBlocks(List.of())
                    .unassignedTasks(List.of())
                    .build();
        }

        // FA-01: No schedule / availability configured (AIB-25 FA-02)
        if (weeklySchedules == null || weeklySchedules.isEmpty()) {
            return WeeklyDistributionPlan.builder()
                    .studentId(studentId)
                    .assignedBlocks(List.of())
                    .unassignedTasks(pendingTasks)
                    .message("Configura tu disponibilidad horaria para activar la distribución automática.")
                    .build();
        }

        // 2. Sort tasks strictly prioritizing HIGH/CRITICAL (isHighPriority = true),
        // and then by priority score descending.
        List<PlanningTask> sortedTasks = pendingTasks.stream()
                .sorted(Comparator.comparing(PlanningTask::isHighPriority).reversed()
                        .thenComparing(Comparator.comparingDouble(PlanningTask::getPriorityScore).reversed()))
                .toList();

        List<ScheduledBlock> assignedBlocks = new ArrayList<>();
        List<PlanningTask> unassignedTasks = new ArrayList<>();
        Map<LocalDate, Integer> dailyMinutesUsed = new HashMap<>(); // RN-03: daily 240-min cap

        // 3. Apply Availability Filter: Create a mutable copy of the weekly schedule
        // strictly excluding any explicitly marked unavailable blocks (AIB-25 / RN-02).
        List<UnavailableBlock> safeUnavailableBlocks = unavailableBlocks != null ? unavailableBlocks : List.of();
        List<MutableDay> availableDays = weeklySchedules.stream()
                .map(ds -> {
                    List<TimeSlot> rawSlots = ds.getAvailableSlots() != null ? ds.getAvailableSlots() : List.of();

                    // Find blocked events strictly for this specific day
                    List<UnavailableBlock> blocksForDay = safeUnavailableBlocks.stream()
                            .filter(ub -> ub.getDate().equals(ds.getDate()))
                            .toList();

                    // Run through the filter algorithm
                    List<TimeSlot> cleanSlots = AvailabilityFilter.removeUnavailableBlocks(rawSlots, blocksForDay);

                    return new MutableDay(ds.getDate(), cleanSlots);
                })
                .toList();

        // AIB-25 FA-01: Check if protected blocks have consumed all available academic
        // time.
        // RN-01: PERSONAL, DESCANSO and SOCIAL blocks are unconditionally excluded.
        int totalAcademicMinutes = availableDays.stream()
                .flatMap(d -> d.slots.stream())
                .mapToInt(s -> (int) Math.round(s.getDurationHours() * 60))
                .sum();
        if (totalAcademicMinutes == 0) {
            boolean hasProtectedBlocks = safeUnavailableBlocks.stream().anyMatch(UnavailableBlock::isProtected);
            return WeeklyDistributionPlan.builder()
                    .studentId(studentId)
                    .assignedBlocks(List.of())
                    .unassignedTasks(pendingTasks)
                    .message(hasProtectedBlocks
                            ? "No hay bloques académicos disponibles. Revisa tu configuración de disponibilidad."
                            : "Configura tu disponibilidad horaria para activar la distribución automática.")
                    .build();
        }

        // AIB-27: Detect proposed overloaded days (uncapped simulation) — must run
        // before the actual capped loop, using clones of availableDays.
        Map<LocalDate, Integer> proposedDailyMinutes = computeProposedDailyMinutes(sortedTasks, availableDays);
        List<OverloadedDayRecord> overloadedDays = proposedDailyMinutes.entrySet().stream()
                .filter(e -> e.getValue() > MAX_MINUTES_PER_DAY)
                .map(e -> OverloadedDayRecord.builder()
                        .date(e.getKey())
                        .excessMinutes(e.getValue() - MAX_MINUTES_PER_DAY)
                        .build())
                .sorted(Comparator.comparing(OverloadedDayRecord::getDate))
                .toList();

        // 4. Distribution Algorithm (greedy: most-priority task → first day with
        // capacity)
        for (PlanningTask task : sortedTasks) {
            // Apply AIB-22.4 correction factor: use correctedEstimatedMinutes when
            // available
            int taskMinutes = task.getCorrectedEstimatedMinutes() != null
                    ? task.getCorrectedEstimatedMinutes()
                    : (int) Math.round(task.getEstimatedHours() * 60);
            double remainingHours = taskMinutes / 60.0;

            for (MutableDay day : availableDays) {
                if (remainingHours <= 0)
                    break;

                // Do not schedule tasks after their deadline
                if (task.getDueDate() != null && day.date.isAfter(task.getDueDate())) {
                    continue;
                }

                // RN-03: Enforce MAX_MINUTES_PER_DAY = 240
                int dayMinutesUsed = dailyMinutesUsed.getOrDefault(day.date, 0);
                if (dayMinutesUsed >= MAX_MINUTES_PER_DAY)
                    continue;
                int remainingDailyCapMinutes = MAX_MINUTES_PER_DAY - dayMinutesUsed;

                for (int i = 0; i < day.slots.size(); i++) {
                    TimeSlot slot = day.slots.get(i);
                    double slotDuration = slot.getDurationHours();

                    if (slotDuration <= 0)
                        continue;

                    // Respect both remaining task duration AND daily cap (RN-03)
                    int slotMinutes = (int) Math.round(slotDuration * 60);
                    int minutesToTake = Math.min(
                            (int) Math.round(remainingHours * 60),
                            Math.min(slotMinutes, remainingDailyCapMinutes));
                    if (minutesToTake <= 0)
                        break;

                    double hoursToTake = minutesToTake / 60.0;
                    LocalTime startTime = slot.getStartTime();
                    LocalTime endTime = startTime.plusMinutes(minutesToTake);

                    assignedBlocks.add(ScheduledBlock.builder()
                            .task(task)
                            .date(day.date)
                            .startTime(startTime)
                            .endTime(endTime)
                            .build());

                    remainingHours -= hoursToTake;
                    dailyMinutesUsed.merge(day.date, minutesToTake, (a, b) -> a + b);
                    remainingDailyCapMinutes -= minutesToTake;

                    // Reduce the slot size or mark as consumed
                    if (hoursToTake < slotDuration) {
                        day.slots.set(i, TimeSlot.builder()
                                .startTime(endTime)
                                .endTime(slot.getEndTime())
                                .build());
                    } else {
                        day.slots.set(i, TimeSlot.builder()
                                .startTime(slot.getEndTime())
                                .endTime(slot.getEndTime())
                                .build()); // Empty slot
                    }

                    if (remainingHours <= 0 || remainingDailyCapMinutes <= 0) {
                        break;
                    }
                }
            }

            // If after looping through the whole week it couldn't fit entirely
            if (remainingHours > 0) {
                unassignedTasks.add(task);
            }
        }

        String overloadMessage = overloadedDays.isEmpty() ? null
                : "Tu plan fue ajustado para respetar tu límite diario de estudio.";

        return WeeklyDistributionPlan.builder()
                .studentId(studentId)
                .assignedBlocks(assignedBlocks)
                .unassignedTasks(unassignedTasks)
                .overloadedDays(overloadedDays)
                .message(overloadMessage)
                .build();
    }

    /**
     * Simulates task distribution WITHOUT the daily cap to compute the proposed
     * per-day minute totals. Used by AIB-27 to detect which days would have been
     * overloaded. Operates on clones of availableDays so the actual state is
     * unaffected.
     */
    private Map<LocalDate, Integer> computeProposedDailyMinutes(
            List<PlanningTask> sortedTasks,
            List<MutableDay> availableDays) {

        // Clone available days — MutableDay constructor already copies the slots list
        List<MutableDay> simDays = availableDays.stream()
                .map(d -> new MutableDay(d.date, d.slots))
                .toList();

        Map<LocalDate, Integer> proposedMinutes = new HashMap<>();

        for (PlanningTask task : sortedTasks) {
            int taskMinutes = task.getCorrectedEstimatedMinutes() != null
                    ? task.getCorrectedEstimatedMinutes()
                    : (int) Math.round(task.getEstimatedHours() * 60);
            double remainingHours = taskMinutes / 60.0;

            for (MutableDay day : simDays) {
                if (remainingHours <= 0)
                    break;
                if (task.getDueDate() != null && day.date.isAfter(task.getDueDate()))
                    continue;

                for (int i = 0; i < day.slots.size(); i++) {
                    TimeSlot slot = day.slots.get(i);
                    double slotDuration = slot.getDurationHours();
                    if (slotDuration <= 0)
                        continue;

                    int slotMinutes = (int) Math.round(slotDuration * 60);
                    int minutesToTake = Math.min((int) Math.round(remainingHours * 60), slotMinutes);
                    if (minutesToTake <= 0)
                        break;

                    proposedMinutes.merge(day.date, minutesToTake, Integer::sum);
                    remainingHours -= minutesToTake / 60.0;

                    LocalTime newStart = slot.getStartTime().plusMinutes(minutesToTake);
                    day.slots.set(i, TimeSlot.builder()
                            .startTime(newStart)
                            .endTime(slot.getEndTime())
                            .build());

                    if (remainingHours <= 0)
                        break;
                }
            }
        }

        return proposedMinutes;
    }

    /**
     * Helper class to keep track of mutable time slots during execution
     */
    private static class MutableDay {
        LocalDate date;
        List<TimeSlot> slots;

        MutableDay(LocalDate date, List<TimeSlot> slots) {
            this.date = date;
            this.slots = new ArrayList<>(slots);
        }
    }
}

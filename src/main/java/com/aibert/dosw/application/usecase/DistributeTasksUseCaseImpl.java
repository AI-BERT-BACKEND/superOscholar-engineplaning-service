package com.aibert.dosw.application.usecase;

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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the Task Distribution Engine (R16).
 * It splits tasks to fit into available time slots, strictly prioritizing
 * HIGH/CRITICAL tasks.
 */
@Service
@RequiredArgsConstructor
public class DistributeTasksUseCaseImpl implements DistributeTasksUseCase {

    private final TaskProviderPort taskProviderPort;
    private final ScheduleProviderPort scheduleProviderPort;

    @Override
    public WeeklyDistributionPlan distribute(String studentId) {

        // 1. Fetch pending tasks, availability, and unavailable blocks
        List<PlanningTask> pendingTasks = taskProviderPort.getPendingTasksByUser(studentId);
        List<DailySchedule> weeklySchedules = scheduleProviderPort.getWeeklySchedule(studentId);
        List<UnavailableBlock> unavailableBlocks = scheduleProviderPort.getUnavailableBlocks(studentId);

        if (pendingTasks == null || pendingTasks.isEmpty() || weeklySchedules == null || weeklySchedules.isEmpty()) {
            return WeeklyDistributionPlan.builder()
                    .studentId(studentId)
                    .assignedBlocks(List.of())
                    .unassignedTasks(pendingTasks != null ? pendingTasks : List.of())
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

        // 3. Apply Availability Filter: Create a mutable copy of the weekly schedule
        // strictly excluding any explicitly marked unavailable blocks.
        List<MutableDay> availableDays = weeklySchedules.stream()
                .map(ds -> {
                    List<TimeSlot> rawSlots = ds.getAvailableSlots() != null ? ds.getAvailableSlots() : List.of();

                    // Find blocked events strictly for this specific day
                    List<UnavailableBlock> blocksForDay = unavailableBlocks.stream()
                            .filter(ub -> ub.getDate().equals(ds.getDate()))
                            .toList();

                    // Run through the filter algorithm
                    List<TimeSlot> cleanSlots = AvailabilityFilter.removeUnavailableBlocks(rawSlots, blocksForDay);

                    return new MutableDay(ds.getDate(), cleanSlots);
                })
                .toList();

        // 4. Distribution Algorithm
        for (PlanningTask task : sortedTasks) {
            double remainingHours = task.getEstimatedHours();

            for (MutableDay day : availableDays) {
                if (remainingHours <= 0)
                    break;

                // Do not schedule tasks after their deadline
                if (task.getDueDate() != null && day.date.isAfter(task.getDueDate())) {
                    continue;
                }

                for (int i = 0; i < day.slots.size(); i++) {
                    TimeSlot slot = day.slots.get(i);
                    double slotDuration = slot.getDurationHours();

                    if (slotDuration <= 0)
                        continue;

                    double hoursToTake = Math.min(remainingHours, slotDuration);
                    int minutesToAdd = (int) Math.round(hoursToTake * 60);

                    LocalTime startTime = slot.getStartTime();
                    LocalTime endTime = startTime.plusMinutes(minutesToAdd);

                    assignedBlocks.add(ScheduledBlock.builder()
                            .task(task)
                            .date(day.date)
                            .startTime(startTime)
                            .endTime(endTime)
                            .build());

                    remainingHours -= hoursToTake;

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

                    if (remainingHours <= 0) {
                        break; // Task fully assigned
                    }
                }
            }

            // If after looping through the whole week it couldn't fit entirely
            if (remainingHours > 0) {
                unassignedTasks.add(task);
            }
        }

        return WeeklyDistributionPlan.builder()
                .studentId(studentId)
                .assignedBlocks(assignedBlocks)
                .unassignedTasks(unassignedTasks)
                .build();
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

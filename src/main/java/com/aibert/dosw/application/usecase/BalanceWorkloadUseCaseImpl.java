package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.balance.BalanceSuggestion;
import com.aibert.dosw.domain.model.balance.DifferentialBalance;
import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.BalanceWorkloadUseCase;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the workload balancing use case (R15).
 * Generates suggestions to move tasks from overloaded days to free days.
 */
@Service
@RequiredArgsConstructor
public class BalanceWorkloadUseCaseImpl implements BalanceWorkloadUseCase {

    private final TaskProviderPort taskProviderPort;
    private final ScheduleProviderPort scheduleProviderPort;

    @Override
    public List<BalanceSuggestion> suggestBalance(String studentId) {

        List<PlanningTask> scheduledTasks = taskProviderPort.getScheduledTasksByUser(studentId);
        List<DailySchedule> weeklySchedules = scheduleProviderPort.getWeeklySchedule(studentId);

        if (weeklySchedules == null || weeklySchedules.isEmpty() || scheduledTasks.isEmpty()) {
            return List.of();
        }

        // 1. Group tasks by their scheduled date
        Map<LocalDate, List<PlanningTask>> tasksByDate = scheduledTasks.stream()
                .filter(t -> t.getScheduledDate() != null)
                .collect(Collectors.groupingBy(PlanningTask::getScheduledDate));

        // 2. Calculate daily balances
        List<DifferentialBalance> dailyBalances = new ArrayList<>();
        for (DailySchedule schedule : weeklySchedules) {
            LocalDate date = schedule.getDate();
            double availableHours = schedule.getTotalAvailableHours();

            double scheduledHours = tasksByDate.getOrDefault(date, List.of()).stream()
                    .mapToDouble(PlanningTask::getEstimatedHours)
                    .sum();

            dailyBalances.add(DifferentialBalance.of(date, availableHours, scheduledHours));
        }

        // 3. Identify Overloaded and Free days
        List<DifferentialBalance> overloadedDays = dailyBalances.stream()
                .filter(DifferentialBalance::isOverloaded)
                .toList();

        List<DifferentialBalance> freeDays = new ArrayList<>(dailyBalances.stream()
                .filter(DifferentialBalance::hasFreeTime)
                .toList());

        if (overloadedDays.isEmpty() || freeDays.isEmpty()) {
            return List.of(); // No rebalancing possible or needed
        }

        // 4. Generate Suggestions
        List<BalanceSuggestion> suggestions = new ArrayList<>();

        for (DifferentialBalance overloadedDay : overloadedDays) {
            List<PlanningTask> tasksOnDay = tasksByDate.getOrDefault(overloadedDay.getDate(), new ArrayList<>());

            // Sort tasks: attempt to move tasks with the lowest priority first
            tasksOnDay.sort(Comparator.comparingDouble(PlanningTask::getPriorityScore));

            double currentScheduledHours = overloadedDay.getScheduledHours();
            double targetMaxHours = overloadedDay.getAvailableHours() * 0.8; // Target below 80%

            for (PlanningTask task : tasksOnDay) {
                if (currentScheduledHours <= targetMaxHours) {
                    break; // The day is no longer overloaded
                }

                // Find a free day that can accommodate the task
                for (int i = 0; i < freeDays.size(); i++) {
                    DifferentialBalance freeDay = freeDays.get(i);

                    // Can we add this task without overloading the free day?
                    double projectedHours = freeDay.getScheduledHours() + task.getEstimatedHours();
                    double maxAllowedHours = freeDay.getAvailableHours() * 0.8;

                    LocalDate dueDate = task.getDueDate();
                    if (projectedHours <= maxAllowedHours
                            && (dueDate == null || !dueDate.isBefore(freeDay.getDate()))) {

                        // Suggest moving it
                        suggestions.add(BalanceSuggestion.builder()
                                .taskToMove(task)
                                .fromDate(overloadedDay.getDate())
                                .toDate(freeDay.getDate())
                                .reason(String.format("Day %s is overloaded. Day %s has free time.",
                                        overloadedDay.getDate(), freeDay.getDate()))
                                .build());

                        // Update current counters
                        currentScheduledHours -= task.getEstimatedHours();

                        // Update the free day's scheduled hours so it's not overloaded with the next
                        // task
                        freeDays.set(i,
                                DifferentialBalance.of(freeDay.getDate(), freeDay.getAvailableHours(), projectedHours));

                        break; // Task successfully assigned to a new day
                    }
                }
            }
        }

        return suggestions;
    }
}

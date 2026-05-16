package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.balance.BalanceResult;
import com.aibert.dosw.domain.model.balance.BalanceSuggestion;
import com.aibert.dosw.domain.model.balance.DifferentialBalance;
import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.BalanceWorkloadUseCase;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the workload balancing use case (R15).
 * Returns the complete balance analysis: weekly load, overloaded/empty days,
 * and suggestions.
 */
@Service
@RequiredArgsConstructor
public class BalanceWorkloadUseCaseImpl implements BalanceWorkloadUseCase {

        private static final String MSG_WELL_BALANCED = "La semana está bien distribuida";
        private static final String MSG_OVERLOADED = "Se detectaron días con sobrecarga, se sugiere redistribuir";
        private static final String MSG_NO_AVAILABILITY = "Configura tu disponibilidad diaria para activar el balanceador.";
        private static final String MSG_NO_TASKS = "No hay tareas registradas para esta semana.";
        private static final int MAX_SUGGESTIONS = 5;

        private final TaskProviderPort taskProviderPort;
        private final ScheduleProviderPort scheduleProviderPort;

        @Override
        public BalanceResult suggestBalance(String studentId, LocalDate weekStartDate) {

                List<PlanningTask> allScheduledTasks = taskProviderPort.getScheduledTasksByUser(studentId);
                List<DailySchedule> weeklySchedules = scheduleProviderPort.getWeeklySchedule(studentId);

                // FA-01: No availability configured
                if (weeklySchedules == null || weeklySchedules.isEmpty()) {
                        return BalanceResult.builder()
                                        .weeklyLoadAnalysis(List.of())
                                        .overloadedDays(List.of())
                                        .emptyDays(List.of())
                                        .balanceSuggestions(List.of())
                                        .message(MSG_NO_AVAILABILITY)
                                        .build();
                }

                // Filter tasks to the requested week range (RN-03: only suggest for the current
                // week)
                LocalDate weekEndDate = weekStartDate.plusDays(6);
                List<PlanningTask> scheduledTasks = (allScheduledTasks == null) ? List.of()
                                : allScheduledTasks.stream()
                                                .filter(t -> t.getScheduledDate() != null
                                                                && !t.getScheduledDate().isBefore(weekStartDate)
                                                                && !t.getScheduledDate().isAfter(weekEndDate))
                                                .toList();

                // FA-02: No tasks for the week
                if (scheduledTasks.isEmpty()) {
                        return BalanceResult.builder()
                                        .weeklyLoadAnalysis(List.of())
                                        .overloadedDays(List.of())
                                        .emptyDays(List.of())
                                        .balanceSuggestions(List.of())
                                        .message(MSG_NO_TASKS)
                                        .build();
                }

                // 1. Group tasks by their scheduled date
                Map<LocalDate, List<PlanningTask>> tasksByDate = scheduledTasks.stream()
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

                // 3. Identify overloaded and free days
                List<DifferentialBalance> overloadedBalances = dailyBalances.stream()
                                .filter(DifferentialBalance::isOverloaded)
                                .toList();
                List<DifferentialBalance> freeBalances = new ArrayList<>(dailyBalances.stream()
                                .filter(DifferentialBalance::hasFreeTime)
                                .toList());

                List<String> overloadedDayLabels = overloadedBalances.stream()
                                .map(b -> formatDate(b.getDate()))
                                .toList();
                List<String> emptyDayLabels = freeBalances.stream()
                                .map(b -> formatDate(b.getDate()))
                                .toList();

                // 4. Generate suggestions if there are overloaded AND free days
                List<BalanceSuggestion> suggestions = new ArrayList<>();
                if (!overloadedBalances.isEmpty() && !freeBalances.isEmpty()) {
                        for (DifferentialBalance overloadedDay : overloadedBalances) {
                                if (suggestions.size() >= MAX_SUGGESTIONS)
                                        break; // RN-03: máximo 5 sugerencias
                                List<PlanningTask> tasksOnDay = tasksByDate.getOrDefault(
                                                overloadedDay.getDate(), new ArrayList<>());
                                tasksOnDay.sort(Comparator.comparingDouble(PlanningTask::getPriorityScore));

                                double currentScheduledHours = overloadedDay.getScheduledHours();
                                double targetMaxHours = overloadedDay.getAvailableHours() * 0.8;

                                for (PlanningTask task : tasksOnDay) {
                                        if (currentScheduledHours <= targetMaxHours)
                                                break;

                                        for (int i = 0; i < freeBalances.size(); i++) {
                                                DifferentialBalance freeDay = freeBalances.get(i);
                                                double projectedHours = freeDay.getScheduledHours()
                                                                + task.getEstimatedHours();
                                                double maxAllowedHours = freeDay.getAvailableHours() * 0.8;
                                                LocalDate dueDate = task.getDueDate();

                                                if (projectedHours <= maxAllowedHours
                                                                && (dueDate == null || !dueDate
                                                                                .isBefore(freeDay.getDate()))) {
                                                        suggestions.add(BalanceSuggestion.builder()
                                                                        .taskToMove(task)
                                                                        .fromDate(overloadedDay.getDate())
                                                                        .toDate(freeDay.getDate())
                                                                        .reason(String.format(
                                                                                        "El día %s está sobrecargado. El día %s tiene tiempo libre.",
                                                                                        formatDate(overloadedDay
                                                                                                        .getDate()),
                                                                                        formatDate(freeDay.getDate())))
                                                                        .build());

                                                        currentScheduledHours -= task.getEstimatedHours();
                                                        freeBalances.set(i,
                                                                        DifferentialBalance.of(freeDay.getDate(),
                                                                                        freeDay.getAvailableHours(),
                                                                                        projectedHours));
                                                        break;
                                                }
                                        }
                                }
                        }
                }

                // 5. Build message
                String message = overloadedDayLabels.isEmpty() ? MSG_WELL_BALANCED : MSG_OVERLOADED;

                return BalanceResult.builder()
                                .weeklyLoadAnalysis(dailyBalances)
                                .overloadedDays(overloadedDayLabels)
                                .emptyDays(emptyDayLabels)
                                .balanceSuggestions(suggestions)
                                .message(message)
                                .build();
        }

        private String formatDate(LocalDate date) {
                return date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "CO"))
                                + " " + date;
        }
}

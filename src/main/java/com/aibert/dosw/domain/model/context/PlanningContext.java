package com.aibert.dosw.domain.model.context;

import com.aibert.dosw.domain.model.balance.DifferentialBalance;
import com.aibert.dosw.domain.model.task.PlanningTask;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

/**
 * Full student context for a given moment.
 * Aggregates the data needed to:
 * - generate AI recommendations
 * - build prompts with precomputed data
 * - apply Java planning rules
 */
@Getter
@Builder
public class PlanningContext {

    private final String userId;
    private final LocalDate date;
    private final List<PlanningTask> pendingTasks;
    private final List<PlanningTask> criticalTasks;
    private final List<DifferentialBalance> weekBalances;
    private final double availableHoursToday;
    private final int totalSemesterCredits;
    private final UserProfile userProfile;

    /**
     * Summary of at-risk subjects for the prompt.
     * Example: "Calculus (needs 4.2 in period 3)".
     *
     * @return summary text or a default message
     */
    public String getRiskySummary() {
        if (pendingTasks == null || pendingTasks.isEmpty()) {
            return "No critical-risk subjects";
        }

        String risky = pendingTasks.stream()
                .filter(t -> t.getSubjectRiskLevel().getPriorityFactor() >= 0.7)
                .map(t -> String.format("%s (needs %.2f in period 3)",
                        t.getSubjectName(),
                        t.getRequiredGradePeriod3()))
                .distinct()
                .collect(Collectors.joining(", "));

        return risky.isBlank()
                ? "No critical-risk subjects"
                : risky;
    }

    /**
     * Returns the most urgent task (highest priority score).
     * Returns null if there are no pending tasks.
     *
     * @return top task or null
     */
    public PlanningTask getTopTask() {
        if (pendingTasks == null || pendingTasks.isEmpty()) {
            return null;
        }
        return pendingTasks.stream()
                .max((a, b) -> Double.compare(
                        a.getPriorityScore(),
                        b.getPriorityScore()))
                .orElse(null);
    }

    /**
     * Summary of the daily plan for the prompt.
     * Example: "2.0h Calculus, 1.5h Programming, 0.5h English".
     *
     * @return daily plan summary
     */
    public String getDailyPlanSummary() {
        if (pendingTasks == null || pendingTasks.isEmpty()) {
            return "No tasks scheduled for today";
        }

        double totalScore = pendingTasks.stream()
                .mapToDouble(PlanningTask::getPriorityScore)
                .sum();

        if (totalScore == 0) {
            return "No tasks with calculated priority";
        }

        return pendingTasks.stream()
                .limit(3)
                .map(t -> {
                    double hoursAssigned = (t.getPriorityScore() / totalScore)
                            * availableHoursToday;
                    return String.format("%.1fh %s",
                            hoursAssigned, t.getSubjectName());
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * Summary of overloaded days for the prompt.
     *
     * @return day names or "None"
     */
    public String getOverloadedDaysSummary() {
        if (weekBalances == null || weekBalances.isEmpty()) {
            return "None";
        }
        String overloaded = weekBalances.stream()
                .filter(DifferentialBalance::isOverloaded)
                .map(b -> b.getDate().getDayOfWeek().toString())
                .collect(Collectors.joining(", "));

        return overloaded.isBlank() ? "None" : overloaded;
    }

    /**
     * Checks whether the week has overloaded days.
     *
     * @return true if at least one day is overloaded
     */
    public boolean hasOverloadedDays() {
        if (weekBalances == null)
            return false;
        return weekBalances.stream()
                .anyMatch(DifferentialBalance::isOverloaded);
    }
}

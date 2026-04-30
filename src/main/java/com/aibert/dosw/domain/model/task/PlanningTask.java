package com.aibert.dosw.domain.model.task;

import com.aibert.dosw.domain.model.context.NoteRiskCalculator;
import com.aibert.dosw.domain.model.context.NoteRiskCalculator.RiskLevel;
import com.aibert.dosw.domain.valueobjects.PriorityScore;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.Getter;

/**
 * Primary entity for the planning engine.
 * Represents an academic task with the data required
 * to calculate priority and scheduling.
 */
@Getter
@Builder
public class PlanningTask {

    private final String id;
    private final String userId;

    private final String title;
    private final String description;
    private final double estimatedHours;
    private final int difficulty; // 1 to 5
    private final LocalDate dueDate;

    private final String subjectName;
    private final int subjectCredits;
    private final double taskWeightInGrade; // 0-100

    private final Double gradePeriod1;
    private final Double gradePeriod2;
    private final double weightPeriod1;
    private final double weightPeriod2;
    private final double weightPeriod3;

    private TaskStatus status;
    private TaskPriority priorityLevel;
    private double priorityScore;
    private LocalDate scheduledDate;

    /**
     * Applies a calculated priority to this task.
     *
     * @param score value object with the calculated score
     */
    public void assignPriority(PriorityScore score) {
        this.priorityScore = score.getFinalScore();
        this.priorityLevel = score.getLevel();
    }

    /**
     * Schedules this task for a specific date.
     *
     * @param date scheduled date
     */
    public void scheduleFor(LocalDate date) {
        this.scheduledDate = date;
        this.status = TaskStatus.SCHEDULED;
    }

    /**
     * Marks the task as overloaded (no available day).
     */
    public void markAsOverloaded() {
        this.status = TaskStatus.OVERLOADED;
    }

    /**
     * Marks the task as completed.
     */
    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
    }

    /**
     * Checks if the task is overdue.
     *
     * @return true if the due date has passed
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(this.dueDate);
    }

    /**
     * Calculates days remaining until the due date.
     * Returns 0 if already overdue.
     *
     * @return remaining days (minimum 0)
     */
    public long getDaysRemaining() {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), this.dueDate);
        return Math.max(days, 0);
    }

    /**
     * Calculates the academic risk level for the subject.
     *
     * @return subject risk level
     */
    public RiskLevel getSubjectRiskLevel() {
        double requiredGrade = NoteRiskCalculator.calculateRequiredGrade(
                this.gradePeriod1, this.weightPeriod1,
                this.gradePeriod2, this.weightPeriod2,
                this.weightPeriod3);
        return NoteRiskCalculator.calculateRiskLevel(requiredGrade);
    }

    /**
     * Calculates the minimum grade required in period 3.
     *
     * @return required minimum grade
     */
    public double getRequiredGradePeriod3() {
        return NoteRiskCalculator.calculateRequiredGrade(
                this.gradePeriod1, this.weightPeriod1,
                this.gradePeriod2, this.weightPeriod2,
                this.weightPeriod3);
    }

    /**
     * Checks if the task is high priority.
     *
     * @return true if CRITICAL or HIGH
     */
    public boolean isHighPriority() {
        return this.priorityLevel == TaskPriority.CRITICAL
                || this.priorityLevel == TaskPriority.HIGH;
    }
}

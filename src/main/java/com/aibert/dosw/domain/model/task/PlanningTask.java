package com.aibert.dosw.domain.model.task;

import com.aibert.dosw.domain.model.context.NoteRiskCalculator;
import com.aibert.dosw.domain.model.context.NoteRiskCalculator.RiskLevel;
import com.aibert.dosw.domain.valueobjects.PriorityScore;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Primary entity for the planning engine.
 * Represents an academic task with the data required
 * to calculate priority and scheduling.
 */
@Getter
@Builder
@Jacksonized
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
    private final Double taskWeightInGrade; // optional weight of this specific task
    private final List<NoteRiskCalculator.CutProgress> evaluationCuts; // Dynamic cuts

    private final TaskType type;
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
     * Applies a discrete priority level with a computed score.
     * Used by the auto-prioritization flow when scoring is rule-based.
     *
     * @param level calculated priority level
     * @param score calculated score
     */
    public void applyPriority(TaskPriority level, double score) {
        if (level != null) {
            this.priorityLevel = level;
        }
        this.priorityScore = Math.max(score, 0.0);
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
        if (this.dueDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(this.dueDate);
    }

    /**
     * Calculates days remaining until the due date.
     * Returns 0 if already overdue.
     *
     * @return remaining days (minimum 0)
     */
    public long getDaysRemaining() {
        if (this.dueDate == null) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(LocalDate.now(), this.dueDate);
        return Math.max(days, 0);
    }

    /**
     * Calculates the academic risk level for the subject.
     *
     * @return subject risk level
     */
    public RiskLevel getSubjectRiskLevel() {
        if (this.evaluationCuts == null || this.evaluationCuts.isEmpty()) {
            return RiskLevel.LOW;
        }
        double requiredGrade = NoteRiskCalculator.calculateRequiredGrade(this.evaluationCuts);
        return NoteRiskCalculator.calculateRiskLevel(requiredGrade);
    }

    public double getRequiredGradeForRemainingCuts() {
        if (this.evaluationCuts == null || this.evaluationCuts.isEmpty()) {
            return 0.0;
        }
        return NoteRiskCalculator.calculateRequiredGrade(this.evaluationCuts);
    }

    /**
     * Checks if the task is high priority.
     * Includes CRITICAL (deadline <24h auto-escalated) and HIGH.
     *
     * @return true if CRITICAL or HIGH per AIB-22 spec
     */
    public boolean isHighPriority() {
        return this.priorityLevel != null
                && (this.priorityLevel == TaskPriority.CRITICAL
                        || this.priorityLevel == TaskPriority.HIGH);
    }

    /**
     * Marks the task as CRITICAL when its deadline is imminent (<24h).
     * Per RN-02: automatically escalated to CRITICAL regardless of other factors.
     */
    public void markAsCriticalAlert() {
        this.priorityLevel = TaskPriority.CRITICAL;
        this.priorityScore = Math.max(this.priorityScore, 100.0); // RN-02: max score
    }
}

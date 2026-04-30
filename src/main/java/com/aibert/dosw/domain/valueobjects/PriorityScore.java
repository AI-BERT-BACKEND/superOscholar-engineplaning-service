package com.aibert.dosw.domain.valueobjects;

import com.aibert.dosw.domain.model.context.NoteRiskCalculator.RiskLevel;
import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Getter;

/**
 * Immutable value object representing a calculated priority score
 * for an academic task.
 *
 * Formula:
 * score = (noteFactor * 0.35)
 * + (proximityFactor * 0.35)
 * + (weightFactor * 0.20)
 * + (creditsFactor * 0.10)
 */
@Getter
public final class PriorityScore {

    private final double finalScore; // 0 to 100
    private final TaskPriority level;

    private PriorityScore(double finalScore, TaskPriority level) {
        this.finalScore = finalScore;
        this.level = level;
    }

    /**
     * Creates a PriorityScore by applying the formula.
     *
     * @param riskLevel         subject risk level
     * @param dueDate           task due date
     * @param taskWeightInGrade task weight in the subject grade (0-100)
     * @param subjectCredits    subject credits
     * @param totalCredits      total semester credits
     * @return calculated PriorityScore
     */
    public static PriorityScore of(
            RiskLevel riskLevel,
            LocalDate dueDate,
            double taskWeightInGrade,
            int subjectCredits,
            int totalCredits) {

        double noteFactor = riskLevel.getPriorityFactor();
        double proximityFactor = calculateProximityFactor(dueDate);
        double weightFactor = taskWeightInGrade / 100.0;
        double creditsFactor = totalCredits > 0
                ? (double) subjectCredits / totalCredits
                : 0.0;

        double rawScore = (noteFactor * 0.35)
                + (proximityFactor * 0.35)
                + (weightFactor * 0.20)
                + (creditsFactor * 0.10);

        double finalScore = Math.min(rawScore * 100.0, 100.0);
        finalScore = Math.round(finalScore * 100.0) / 100.0;

        TaskPriority level = assignLevel(finalScore);

        return new PriorityScore(finalScore, level);
    }

    /**
     * Creates a PriorityScore from a known score.
     * Useful for tests or reconstruction from persistence.
     *
     * @param score score between 0 and 100
     * @return PriorityScore with an assigned level
     */
    public static PriorityScore fromScore(double score) {
        double clamped = Math.min(Math.max(score, 0.0), 100.0);
        return new PriorityScore(clamped, assignLevel(clamped));
    }

    private static double calculateProximityFactor(LocalDate dueDate) {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        if (daysLeft <= 0)
            return 1.00;
        if (daysLeft <= 1)
            return 0.95;
        if (daysLeft <= 3)
            return 0.80;
        if (daysLeft <= 7)
            return 0.60;
        if (daysLeft <= 14)
            return 0.30;
        return 0.10;
    }

    private static TaskPriority assignLevel(double score) {
        if (score >= 75)
            return TaskPriority.CRITICAL;
        if (score >= 50)
            return TaskPriority.HIGH;
        if (score >= 25)
            return TaskPriority.MEDIUM;
        return TaskPriority.LOW;
    }

    /**
     * Checks if the score is critical.
     *
     * @return true if level is CRITICAL
     */
    public boolean isCritical() {
        return this.level == TaskPriority.CRITICAL;
    }

    @Override
    public String toString() {
        return String.format("PriorityScore{score=%.2f, level=%s}",
                finalScore, level);
    }
}

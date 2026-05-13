package com.aibert.dosw.domain.valueobjects;

import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Getter;

/**
 * Immutable value object representing a calculated priority score
 * for an academic task.
 *
 * The priority is calculated based on:
 * 1. Deadline proximity (Proximity Factor)
 * 2. Subject weight (Weight Factor)
 * 3. Estimated time (Time Factor)
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
     * Calculates the priority score using default weights (40/35/25).
     * Convenience method for domain-only contexts and backward compatibility.
     *
     * @param dueDate        Task deadline
     * @param subjectWeight  Weight of the subject or task (0.0–5.0 scale per R14 spec)
     * @param estimatedHours Estimated hours required to complete the task
     * @return Calculated PriorityScore
     */
    public static PriorityScore calculate(
            LocalDate dueDate,
            double subjectWeight,
            double estimatedHours) {
        return calculate(dueDate, subjectWeight, estimatedHours, 0.40, 0.35, 0.25);
    }

    /**
     * Calculates the priority score with configurable weights.
     * The weights should sum to 1.0 for the score to range from 0 to 100.
     *
     * <p><b>RN-02:</b> If the deadline is within 24 hours, the task is automatically
     * escalated to ALTA regardless of other factors.</p>
     *
     * @param dueDate         Task deadline
     * @param subjectWeight   Academic weight of the subject (0.0–5.0 scale per R14)
     * @param estimatedHours  Estimated hours required (max 72h per R14)
     * @param wProximity      Weight for deadline proximity factor
     * @param wAcademic       Weight for academic weight factor
     * @param wTime           Weight for estimated time factor
     * @return Calculated PriorityScore
     */
    public static PriorityScore calculate(
            LocalDate dueDate,
            double subjectWeight,
            double estimatedHours,
            double wProximity,
            double wAcademic,
            double wTime) {

        // RN-02: Deadline < 24h → automáticamente ALTA con score máximo
        if (dueDate != null) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            if (daysLeft <= 0) {
                return new PriorityScore(100.0, TaskPriority.CRITICA);
            }
        }

        double proximityFactor = calculateProximityFactor(dueDate);
        // R14 spec: academicWeight es 0.0–5.0 → se normaliza dividiendo entre 5.0
        double weightFactor = Math.min(subjectWeight / 5.0, 1.0);
        // R14 spec: estimatedTime máximo 72h
        double timeFactor = calculateTimeFactor(Math.min(estimatedHours, 72.0));

        double rawScore = (proximityFactor * wProximity)
                + (weightFactor * wAcademic)
                + (timeFactor * wTime);

        double finalScore = Math.min(rawScore * 100.0, 100.0);
        finalScore = Math.round(finalScore * 100.0) / 100.0;

        TaskPriority level = assignLevel(finalScore);

        return new PriorityScore(finalScore, level);
    }

    /**
     * Calculates the proximity factor based on remaining days.
     * Closer deadlines yield higher factors.
     */
    private static double calculateProximityFactor(LocalDate dueDate) {
        if (dueDate == null) {
            return 0.10;
        }
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        if (daysLeft <= 0)
            return 1.00;
        if (daysLeft <= 1)
            return 0.95; // Deadline mañana → factor muy alto
        if (daysLeft <= 3)
            return 0.80;
        if (daysLeft <= 7)
            return 0.60;
        if (daysLeft <= 14)
            return 0.30;
        return 0.10;
    }

    /**
     * Calculates the time factor.
     * Longer tasks yield a higher priority factor to ensure they are started early.
     * Assumes a 20-hour task represents the maximum effort (factor 1.0).
     */
    private static double calculateTimeFactor(double estimatedHours) {
        double factor = estimatedHours / 20.0;
        return Math.min(factor, 1.0);
    }

    /**
     * Assigns the categorical priority level based on the numerical score.
     * Thresholds per R14 spec:
     * <ul>
     *   <li>ALTA  — score ≥ 70</li>
     *   <li>MEDIA — score 40–69</li>
     *   <li>BAJA  — score &lt; 40</li>
     * </ul>
     */
    private static TaskPriority assignLevel(double score) {
        if (score >= 70)
            return TaskPriority.ALTA;
        if (score >= 40)
            return TaskPriority.MEDIA;
        return TaskPriority.BAJA;
    }
}

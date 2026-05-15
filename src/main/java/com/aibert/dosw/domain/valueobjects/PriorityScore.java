package com.aibert.dosw.domain.valueobjects;

import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * Calculates the priority score using default weights (40/40/20).
     * Convenience method for domain-only contexts.
     *
     * @param dueDate        Task deadline
     * @param subjectWeight  Academic weight of the subject (0.0–1.0)
     * @param estimatedHours Estimated hours required to complete the task
     * @return Calculated PriorityScore
     */
    public static PriorityScore calculate(
            LocalDate dueDate,
            double subjectWeight,
            double estimatedHours) {
        return calculate(dueDate, subjectWeight, estimatedHours, 0.40, 0.40, 0.20);
    }

    /**
     * Calculates the priority score with configurable weights.
     * The weights should sum to 1.0 for the score to range from 0 to 100.
     *
     * <p>
     * <b>RN-02:</b> If the deadline is within 24 hours, the task is automatically
     * escalated to CRITICA regardless of other factors.
     * </p>
     *
     * @param dueDate        Task deadline
     * @param subjectWeight  Academic weight of the subject (0.0–1.0)
     * @param estimatedHours Estimated hours required
     * @param wProximity     Weight for deadline proximity factor
     * @param wAcademic      Weight for academic weight factor
     * @param wTime          Weight for estimated time factor
     * @return Calculated PriorityScore
     */
    public static PriorityScore calculate(
            LocalDate dueDate,
            double subjectWeight,
            double estimatedHours,
            double wProximity,
            double wAcademic,
            double wTime) {

        if (dueDate == null) {
            return new PriorityScore(0.0, TaskPriority.BAJA);
        }

        long hoursLeft = ChronoUnit.HOURS.between(LocalDateTime.now(), dueDate.atTime(23, 59));

        // RN-02: Deadline < 24h → CRITICA con score máximo
        if (hoursLeft <= 24) {
            return new PriorityScore(100.0, TaskPriority.CRITICA);
        }

        double proximityScore = calculateProximityScore(hoursLeft);
        double academicScore = Math.min(Math.max(subjectWeight, 0.0), 1.0) * 100.0;
        double timeScore = calculateTimeScore(estimatedHours);

        double finalScore = (academicScore * wAcademic)
                + (proximityScore * wProximity)
                + (timeScore * wTime);

        finalScore = Math.min(Math.max(finalScore, 0.0), 100.0);
        finalScore = Math.round(finalScore * 100.0) / 100.0;

        TaskPriority level = assignLevel(finalScore);

        return new PriorityScore(finalScore, level);
    }

    /**
     * Calculates the proximity score based on remaining hours.
     */
    private static double calculateProximityScore(long hoursLeft) {
        if (hoursLeft <= 24) {
            return 100.0;
        }
        if (hoursLeft <= 48) {
            return 80.0;
        }
        if (hoursLeft <= 120) {
            return 60.0;
        }
        return 40.0;
    }

    /**
     * Calculates the time score from estimated hours.
     */
    private static double calculateTimeScore(double estimatedHours) {
        double estimatedMinutes = Math.max(estimatedHours, 0.0) * 60.0;
        return Math.min(estimatedMinutes / 3.0, 100.0);
    }

    /**
     * Assigns the categorical priority level based on the numerical score.
     * Thresholds per R14 spec:
     * <ul>
     * <li>ALTA — score ≥ 70</li>
     * <li>MEDIA — score 40–69</li>
     * <li>BAJA — score &lt; 40</li>
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

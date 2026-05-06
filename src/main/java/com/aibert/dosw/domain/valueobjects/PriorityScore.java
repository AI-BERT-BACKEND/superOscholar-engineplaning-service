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
     * Calculates the priority score based on weight, deadline, and estimated time.
     *
     * @param dueDate        Task deadline
     * @param subjectWeight  Weight of the subject or task (0-100)
     * @param estimatedHours Estimated hours required to complete the task
     * @return Calculated PriorityScore
     */
    public static PriorityScore calculate(
            LocalDate dueDate,
            double subjectWeight,
            double estimatedHours) {

        double proximityFactor = calculateProximityFactor(dueDate);
        double weightFactor = Math.min(subjectWeight / 100.0, 1.0);
        double timeFactor = calculateTimeFactor(estimatedHours);

        // Mathematical logic: 40% proximity, 35% weight, 25% estimated time
        double rawScore = (proximityFactor * 0.40)
                + (weightFactor * 0.35)
                + (timeFactor * 0.25);

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
            return 0.95;
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
     */
    private static TaskPriority assignLevel(double score) {
        if (score >= 75)
            return TaskPriority.CRITICAL;
        if (score >= 50)
            return TaskPriority.HIGH;
        if (score >= 25)
            return TaskPriority.MEDIUM;
        return TaskPriority.LOW;
    }
}

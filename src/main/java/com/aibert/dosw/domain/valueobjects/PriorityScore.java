package com.aibert.dosw.domain.valueobjects;

import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;

/**
 * Immutable value object representing a calculated priority score
 * for an academic task (AIB-22).
 *
 * <p>
 * The priority is calculated based on three weighted factors:
 * </p>
 * <ol>
 * <li><b>Deadline proximity</b> — how soon the task is due</li>
 * <li><b>Academic weight</b> — percentage the subject represents in the
 * semester load (RN-03)</li>
 * <li><b>Estimated time</b> — corrected duration factor (AIB-22.4)</li>
 * </ol>
 *
 * <p>
 * <b>RN-02:</b> A task with a deadline within 24 hours is automatically
 * escalated
 * to {@link TaskPriority#CRITICAL} regardless of other factors.
 * </p>
 */
@Getter
public final class PriorityScore {

    private final double finalScore; // 0.0 – 100.0
    private final TaskPriority level;

    private PriorityScore(double finalScore, TaskPriority level) {
        this.finalScore = finalScore;
        this.level = level;
    }

    /**
     * Calculates the priority score using default weights (40 / 40 / 20).
     * Convenience factory for domain-only contexts without external configuration.
     *
     * @param dueDate        Task deadline (may be null)
     * @param subjectWeight  Academic weight of the subject (0.0–1.0)
     * @param estimatedHours Estimated hours required to complete the task
     * @return Calculated PriorityScore
     */
    public static PriorityScore calculate(
            LocalDate dueDate,
            double subjectWeight,
            double estimatedHours) {
        LocalDateTime dueDateTime = dueDate != null ? dueDate.atTime(23, 59) : null;
        return calculate(dueDateTime, subjectWeight, estimatedHours, 0.40, 0.40, 0.20);
    }

    /**
     * Calculates the priority score with fully configurable weights.
     *
     * <p>
     * The three weights should sum to 1.0 so the final score stays in [0, 100].
     * </p>
     *
     * <p>
     * <b>RN-02 (AIB-22):</b> If {@code hoursLeft ≤ 24}, the task is automatically
     * escalated to {@link TaskPriority#CRITICAL} with a score of 100.0.
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
        LocalDateTime dueDateTime = dueDate != null ? dueDate.atTime(23, 59) : null;
        return calculate(dueDateTime, subjectWeight, estimatedHours, wProximity, wAcademic, wTime);
    }

    /**
     * Calculates the priority score with fully configurable weights.
     *
     * @param dueDateTime    Task deadline with time
     * @param subjectWeight  Academic weight of the subject (0.0–1.0)
     * @param estimatedHours Estimated hours required
     * @param wProximity     Weight for deadline proximity factor
     * @param wAcademic      Weight for academic weight factor
     * @param wTime          Weight for estimated time factor
     * @return Calculated PriorityScore
     */
    public static PriorityScore calculate(
            LocalDateTime dueDateTime,
            double subjectWeight,
            double estimatedHours,
            double wProximity,
            double wAcademic,
            double wTime) {

        // FA-02: No deadline → LOW priority with zero score
        if (dueDateTime == null) {
            return new PriorityScore(0.0, TaskPriority.LOW);
        }

        long hoursLeft = ChronoUnit.HOURS.between(LocalDateTime.now(), dueDateTime);

        // RN-02: Deadline < 24 h → CRITICAL with maximum score
        if (hoursLeft <= 24) {
            return new PriorityScore(100.0, TaskPriority.CRITICAL);
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
     * Calculates the proximity score based on remaining hours until deadline.
     */
    private static double calculateProximityScore(long hoursLeft) {
        if (hoursLeft <= 24)
            return 100.0;
        if (hoursLeft <= 48)
            return 80.0;
        if (hoursLeft <= 120)
            return 60.0;
        return 40.0;
    }

    /**
     * Calculates the time factor score from estimated hours (AIB-22.4).
     */
    private static double calculateTimeScore(double estimatedHours) {
        double estimatedMinutes = Math.max(estimatedHours, 0.0) * 60.0;
        return Math.min(estimatedMinutes / 3.0, 100.0);
    }

    /**
     * Assigns the categorical priority level based on the numerical score.
     *
     * <ul>
     * <li>{@link TaskPriority#HIGH} — score ≥ 70</li>
     * <li>{@link TaskPriority#MEDIUM} — score 40–69</li>
     * <li>{@link TaskPriority#LOW} — score &lt; 40</li>
     * </ul>
     *
     * Note: {@link TaskPriority#CRITICAL} is assigned directly when hoursLeft ≤ 24,
     * before this method is ever called.
     */
    private static TaskPriority assignLevel(double score) {
        if (score >= 70)
            return TaskPriority.HIGH;
        if (score >= 40)
            return TaskPriority.MEDIUM;
        return TaskPriority.LOW;
    }
}

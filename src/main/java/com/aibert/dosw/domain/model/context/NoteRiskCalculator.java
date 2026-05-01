package com.aibert.dosw.domain.model.context;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Academic risk calculator per subject.
 * Determines the grade required in the remaining evaluations
 * to pass, and classifies the risk level.
 *
 * Minimum passing grade at ECI: 3.0 / 5.0
 */
@UtilityClass
public class NoteRiskCalculator {

    private static final double PASSING_GRADE = 3.0;
    private static final double MAX_GRADE = 5.0;

    /**
     * Record representing the progress of a single evaluation cut.
     *
     * @param grade  current grade (null if not yet graded)
     * @param weight weight of this cut (0.0 to 1.0, or 0 to 100)
     */
    public record CutProgress(Double grade, double weight) {}

    /**
     * Calculates the minimum grade required in the remaining evaluations
     * to pass the subject, dynamically supporting any number of cuts.
     *
     * @param cuts List of all evaluation cuts for the subject
     * @return minimum required average grade in the remaining cuts
     */
    public static double calculateRequiredGrade(List<CutProgress> cuts) {
        double accumulated = 0.0;
        double remainingWeight = 0.0;

        for (CutProgress cut : cuts) {
            // Normalize weight assuming it might come as 0-100 or 0-1
            double normalizedWeight = cut.weight() > 1.0 ? cut.weight() / 100.0 : cut.weight();
            
            if (cut.grade() != null) {
                accumulated += cut.grade() * normalizedWeight;
            } else {
                remainingWeight += normalizedWeight;
            }
        }

        if (remainingWeight <= 0.0) {
            // All cuts graded. No more remaining weight.
            return accumulated >= PASSING_GRADE ? 0.0 : MAX_GRADE; 
        }

        double required = (PASSING_GRADE - accumulated) / remainingWeight;
        return Math.round(required * 100.0) / 100.0;
    }

    /**
     * Determines the risk level based on the required grade.
     *
     * @param requiredGrade minimum required grade in the remaining cuts
     * @return academic risk level
     */
    public static RiskLevel calculateRiskLevel(double requiredGrade) {
        if (requiredGrade > MAX_GRADE)
            return RiskLevel.LOST;
        if (requiredGrade >= 4.5)
            return RiskLevel.CRITICAL;
        if (requiredGrade >= 4.0)
            return RiskLevel.HIGH;
        if (requiredGrade >= 3.5)
            return RiskLevel.MODERATE;
        return RiskLevel.LOW;
    }

    /**
     * Academic risk levels for a subject.
     */
    @Getter
    @RequiredArgsConstructor
    public enum RiskLevel {

        LOST(1.0, "Lost - Mathematically impossible to pass"),
        CRITICAL(0.9, "Critical - Needs >= 4.5 in remaining evaluations"),
        HIGH(0.7, "High - Needs >= 4.0 in remaining evaluations"),
        MODERATE(0.5, "Moderate - Needs >= 3.5 in remaining evaluations"),
        LOW(0.3, "Low - Doing well in the subject");

        private final double priorityFactor;
        private final String description;
    }
}

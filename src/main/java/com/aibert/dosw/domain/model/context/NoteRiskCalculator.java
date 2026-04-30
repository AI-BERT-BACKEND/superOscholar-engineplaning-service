package com.aibert.dosw.domain.model.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Academic risk calculator per subject.
 * Determines the grade required in the third evaluation
 * to pass, and classifies the risk level.
 *
 * Minimum passing grade at ECI: 3.0 / 5.0
 */
@UtilityClass
public class NoteRiskCalculator {

    private static final double PASSING_GRADE = 3.0;
    private static final double MAX_GRADE = 5.0;

    /**
     * Calculates the minimum grade required in the third evaluation
     * to pass the subject.
     *
     * @param gradeP1  first evaluation grade (null if missing)
     * @param weightP1 first evaluation weight (e.g., 0.30)
     * @param gradeP2  second evaluation grade (null if missing)
     * @param weightP2 second evaluation weight (e.g., 0.30)
     * @param weightP3 third evaluation weight (e.g., 0.40)
     * @return minimum required grade in the third evaluation
     */
    public static double calculateRequiredGrade(
            Double gradeP1, double weightP1,
            Double gradeP2, double weightP2,
            double weightP3) {

        double accumulated = 0.0;

        if (gradeP1 != null) {
            accumulated += gradeP1 * weightP1;
        }
        if (gradeP2 != null) {
            accumulated += gradeP2 * weightP2;
        }

        if (weightP3 == 0.0) {
            return MAX_GRADE;
        }

        double required = (PASSING_GRADE - accumulated) / weightP3;
        return Math.round(required * 100.0) / 100.0;
    }

    /**
     * Determines the risk level based on the required grade.
     *
     * @param requiredGrade minimum required grade in the third evaluation
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
        CRITICAL(0.9, "Critical - Needs >= 4.5 in evaluation 3"),
        HIGH(0.7, "High - Needs >= 4.0 in evaluation 3"),
        MODERATE(0.5, "Moderate - Needs >= 3.5 in evaluation 3"),
        LOW(0.3, "Low - Doing well in the subject");

        /**
         * Grade factor for priority calculation.
         * Higher risk = higher factor = higher priority.
         */
        private final double priorityFactor;
        private final String description;
    }
}

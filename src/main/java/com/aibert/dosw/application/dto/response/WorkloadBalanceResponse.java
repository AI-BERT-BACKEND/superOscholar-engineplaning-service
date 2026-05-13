package com.aibert.dosw.application.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing the complete workload balance analysis for a week (R15).
 * Contains daily load analysis, overloaded/empty day detection, and redistribution suggestions.
 */
@Getter
@Builder
public class WorkloadBalanceResponse {
    private final String studentId;

    /**
     * Per-day analysis: hours assigned, hours available, and % occupancy.
     * Maps to weeklyLoadAnalysis in the R15 spec output.
     */
    private final List<DayBalanceResponse> weeklyLoadAnalysis;

    /**
     * Days where the load exceeds 80% of declared availability (RN-01).
     */
    private final List<String> overloadedDays;

    /**
     * Days where the load is below 20% of declared availability (RN-02).
     */
    private final List<String> emptyDays;

    /**
     * Suggestions: which tasks to move and to which days to balance the week.
     */
    private final List<BalanceSuggestionResponse> balanceSuggestions;

    /**
     * Human-readable result message per R15 spec:
     * "La semana está bien distribuida" or
     * "Se detectaron días con sobrecarga, se sugiere redistribuir"
     */
    private final String message;
}


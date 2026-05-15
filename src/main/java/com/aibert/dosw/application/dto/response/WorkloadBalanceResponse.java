package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing the complete workload balance analysis for
 * a week (R15).
 * Contains daily load analysis, overloaded/empty day detection, and
 * redistribution suggestions.
 */
@Getter
@Builder
@Schema(description = "Complete weekly workload balance analysis and suggestions")
public class WorkloadBalanceResponse {
    @Schema(description = "Student identifier for the analysis", example = "student-123")
    private final String studentId;

    /**
     * Per-day analysis: hours assigned, hours available, and % occupancy.
     * Maps to weeklyLoadAnalysis in the R15 spec output.
     */
    @Schema(description = "Per-day load analysis including availability and occupancy")
    private final List<DayBalanceResponse> weeklyLoadAnalysis;

    /**
     * Days where the load exceeds 80% of declared availability (RN-01).
     */
    @Schema(description = "Days where the load exceeds the overload threshold", example = "[\"2026-05-13\"]")
    private final List<String> overloadedDays;

    /**
     * Days where the load is below 20% of declared availability (RN-02).
     */
    @Schema(description = "Days where the load is below the minimum threshold", example = "[\"2026-05-15\"]")
    private final List<String> emptyDays;

    /**
     * Suggestions: which tasks to move and to which days to balance the week.
     */
    @Schema(description = "Suggested task moves to improve weekly balance")
    private final List<BalanceSuggestionResponse> balanceSuggestions;

    /**
     * Human-readable result message per R15 spec:
     * "La semana está bien distribuida" or
     * "Se detectaron días con sobrecarga, se sugiere redistribuir"
     */
    @Schema(description = "Human-readable summary message", example = "Workload is balanced for the week")
    private final String message;
}

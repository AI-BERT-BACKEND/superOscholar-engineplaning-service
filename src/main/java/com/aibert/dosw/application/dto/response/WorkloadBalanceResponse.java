package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    @Schema(description = "Student identifier for the analysis", example = "100095379")
    private final String studentId;

    /**
     * Per-day analysis: hours assigned, hours available, and % occupancy.
     * Map key = ISO date (2026-05-13). Always 7 entries (Mon–Sun) per AIB-23 spec.
     */
    @Schema(description = "Per-day load analysis keyed by date (ISO format). Always 7 entries, one per day of the week.")
    private final Map<LocalDate, DayBalanceResponse> weeklyLoadAnalysis;

    /**
     * Days where the load exceeds 80% of declared availability (RN-01).
     */
    @Schema(description = "Days where the load exceeds the overload threshold (>80%)", example = "[\"2026-05-13\"]")
    private final List<LocalDate> overloadedDays;

    /**
     * Days where the load is below 20% of declared availability (RN-02).
     */
    @Schema(description = "Days where the load is below the minimum threshold (<20%)", example = "[\"2026-05-15\"]")
    private final List<LocalDate> emptyDays;

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

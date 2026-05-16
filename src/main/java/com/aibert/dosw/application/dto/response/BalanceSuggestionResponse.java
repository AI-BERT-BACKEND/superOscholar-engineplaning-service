package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a concrete suggestion to move a task between days
 * in order to balance the weekly workload (AIB-23).
 */
@Getter
@Builder
@Schema(description = "Suggestion to move a task from an overloaded day to a lighter day to improve balance")
public class BalanceSuggestionResponse {

    @Schema(description = "Identifier of the task suggested for moving", example = "task-abc-123")
    private final String taskId;

    @Schema(description = "Title of the task for display purposes", example = "Parcial de Cálculo")
    private final String taskTitle;

    @Schema(description = "Current (overloaded) day from which the task should be moved", example = "2026-05-13")
    private final LocalDate fromDay;

    @Schema(description = "Suggested destination day with available capacity", example = "2026-05-15")
    private final LocalDate toDay;

    @Schema(description = "Justification for this suggestion (max 200 characters)", example = "El día lunes está sobrecargado. El día miércoles tiene tiempo libre.")
    private final String reason;
}

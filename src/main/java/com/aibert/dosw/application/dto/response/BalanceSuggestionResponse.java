package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing a balance suggestion.
 */
@Getter
@Builder
@Schema(description = "Suggestion to move a task to balance the weekly workload")
public class BalanceSuggestionResponse {
    @Schema(description = "Task suggested to move")
    private final PrioritizedTaskResponse task;

    @Schema(description = "Original date of the task before the suggested move", example = "2026-05-13")
    private final LocalDate fromDate;

    @Schema(description = "Suggested target date for the task", example = "2026-05-15")
    private final LocalDate toDate;

    @Schema(description = "Short reason for the suggestion", example = "Overloaded day")
    private final String reason;

    @Schema(description = "Human-readable suggestion message", example = "Move this task to improve balance")
    private final String suggestionMessage;
}

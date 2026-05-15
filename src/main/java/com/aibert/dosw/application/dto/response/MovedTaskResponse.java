package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a task that was relocated during dynamic rebalancing (R17).
 * Contains the original block and new block for frontend display.
 */
@Getter
@Builder
@Schema(description = "Task relocation details produced by a rebalance")
public class MovedTaskResponse {

    /** ID of the relocated task. */
    @Schema(description = "Identifier of the relocated task", example = "task-456")
    private final String taskId;

    /** Title of the relocated task. */
    @Schema(description = "Title of the relocated task", example = "Essay Draft")
    private final String taskTitle;

    /** Original date before rebalancing. */
    @Schema(description = "Original date before rebalancing", example = "2026-05-12")
    private final LocalDate originalDate;

    /** Original start time before rebalancing. */
    @Schema(description = "Original start time before rebalancing", example = "09:00:00")
    private final LocalTime originalStartTime;

    /** New date after rebalancing. */
    @Schema(description = "New date after rebalancing", example = "2026-05-14")
    private final LocalDate newDate;

    /** New start time after rebalancing. */
    @Schema(description = "New start time after rebalancing", example = "11:00:00")
    private final LocalTime newStartTime;

    /** Human-readable reason for the relocation. */
    @Schema(description = "Human-readable reason for the relocation", example = "Conflict with another task")
    private final String reason;
}

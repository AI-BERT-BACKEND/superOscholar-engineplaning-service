package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Detail of a single pending task after the correction factor has been applied
 * (AIB-22.4).
 */
@Getter
@Builder
@Schema(description = "A pending task with its estimation updated by the correction factor")
public class UpdatedEstimateResponse {

    @Schema(description = "Task identifier", example = "task-xyz-456")
    private final String taskId;

    @Schema(description = "Task title", example = "Parcial de Cálculo")
    private final String title;

    @Schema(description = "Task type", example = "EXAMEN")
    private final String taskType;

    @Schema(description = "Original estimated duration before the correction factor was applied, in minutes", example = "120")
    private final int originalEstimatedMinutes;

    @Schema(description = "New estimated duration after applying the correction factor, in minutes", example = "156")
    private final int adjustedEstimatedMinutes;
}

package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * API DTO representing a single task identified as at-risk (AIB-22.3).
 */
@Getter
@Builder
@Schema(description = "Task identified as having HIGH or MEDIUM academic risk")
public class RiskTaskDetailResponse {

    @Schema(description = "Task identifier", example = "task-abc")
    private final String taskId;

    @Schema(description = "Task title", example = "Parcial de Álgebra Lineal")
    private final String title;

    @Schema(description = "Risk level: HIGH (available < 70% of estimated) or MEDIUM (70%–85%)", example = "HIGH")
    private final String riskLevel;

    @Schema(description = "Total available minutes until the task deadline", example = "120")
    private final int availableMinutes;

    @Schema(description = "Estimated work duration in minutes (adjusted by correction factor)", example = "240")
    private final int estimatedDurationMinutes;

    @Schema(description = "Academic weight of the subject in the semester grade (0.0–1.0)", example = "0.35")
    private final double academicWeight;
}

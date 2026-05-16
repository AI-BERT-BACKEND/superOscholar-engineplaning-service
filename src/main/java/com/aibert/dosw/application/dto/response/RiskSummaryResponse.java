package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * API DTO summarising the overall academic risk for a student (AIB-22.3).
 */
@Getter
@Builder
@Schema(description = "Summary of the overall academic risk across all active tasks")
public class RiskSummaryResponse {

    @Schema(description = "Total number of tasks at risk (HIGH + MEDIUM)", example = "3")
    private final int totalAtRisk;

    @Schema(description = "Percentage of the total active workload affected by risk (0.0–100.0)", example = "42.5")
    private final double affectedLoadPercentage;
}

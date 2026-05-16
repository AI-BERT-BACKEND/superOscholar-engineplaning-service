package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Top-level API response for high-risk task detection (AIB-22.3).
 */
@Getter
@Builder
@Schema(description = "Result of the high-risk task detection analysis")
public class HighRiskDetectionResponse {

    @Schema(description = "Tasks with HIGH or MEDIUM academic risk, sorted by academic weight (desc) then risk level")
    private final List<RiskTaskDetailResponse> highRiskTasks;

    @Schema(description = "Overall risk summary for the student")
    private final RiskSummaryResponse riskSummary;

    @Schema(description = "Human-readable summary message", example = "Se detectaron 2 tarea(s) en riesgo académico.")
    private final String message;
}

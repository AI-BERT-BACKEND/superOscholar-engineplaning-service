package com.aibert.dosw.recommendation.application.dto.response;

import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO for AIB-22.2: critical task recommendations.
 */
@Getter
@Builder
@Schema(description = "Critical task recommendations for a student")
public class CriticalRecommendationsResponse {

    @Schema(description = "List of up to 3 critical task recommendations ordered by priority and nearest deadline")
    private final List<PrioritizedTaskResponse> criticalRecommendations;

    @Schema(description = "Total number of critical tasks detected (HIGH/CRITICAL, active, within 48 hours)", example = "2")
    private final Integer criticalCount;

    @Schema(description = "Human-readable summary message", example = "Tienes 2 tarea(s) crítica(s) que requieren atención inmediata")
    private final String message;
}

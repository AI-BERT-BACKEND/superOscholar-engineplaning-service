package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Critical task recommendations")
public class CriticalRecommendationsResponse {
    @Schema(description = "List of up to 3 critical recommendations")
    private final List<PrioritizedTaskResponse> criticalRecommendations;

    @Schema(description = "Total number of critical tasks", example = "2")
    private final Integer criticalCount;

    @Schema(description = "Message for the student", example = "You have 2 critical tasks that require immediate attention")
    private final String message;
}

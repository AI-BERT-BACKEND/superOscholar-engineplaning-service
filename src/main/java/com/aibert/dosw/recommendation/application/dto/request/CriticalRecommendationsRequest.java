package com.aibert.dosw.recommendation.application.dto.request;

import com.aibert.dosw.application.dto.request.CriticalTaskCandidateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Optional request body for AIB-22.2 critical recommendations.
 * If {@code orderedTasks} is omitted the engine fetches and prioritizes
 * automatically.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Optional input for critical recommendations. If orderedTasks is omitted, the engine fetches and prioritizes automatically.")
public class CriticalRecommendationsRequest {

    @Schema(description = "Pre-computed ordered task list from AIB-22 (optional). If not provided, the planning engine recalculates automatically.")
    private List<CriticalTaskCandidateRequest> orderedTasks;
}

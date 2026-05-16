package com.aibert.dosw.recommendation.domain.ports.in;

import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.recommendation.application.dto.response.CriticalRecommendationsResponse;
import java.util.List;

/**
 * Input port for AIB-22.2: critical task recommendations.
 * Filters a list of prioritized tasks and returns up to 3 critical ones
 * (HIGH/CRITICAL priority, active status, deadline within 48 hours).
 * If no ordered list is provided, the engine fetches and prioritizes
 * internally.
 */
public interface CriticalRecommendationsUseCase {

    /**
     * Returns up to 3 critical task recommendations for the given student.
     *
     * @param studentId        the student identifier
     * @param orderedTasks     pre-computed prioritized task list, or {@code null}
     *                         to
     *                         trigger internal prioritization
     * @param forceRecalculate when {@code true} and {@code orderedTasks} is empty,
     *                         forces a fresh priority recalculation
     * @return critical recommendations response
     */
    CriticalRecommendationsResponse getRecommendations(
            String studentId,
            List<PrioritizedTaskResponse> orderedTasks,
            boolean forceRecalculate);
}

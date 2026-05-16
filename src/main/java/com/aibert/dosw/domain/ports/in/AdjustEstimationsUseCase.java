package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.application.dto.request.AdjustEstimationsRequest;
import com.aibert.dosw.application.dto.response.AdjustEstimationsResponse;

/**
 * Input port for automatic estimation adjustment (AIB-22.4).
 *
 * <p>
 * Learns from historical actual vs estimated times per task type and applies
 * a personalised correction factor to pending tasks of the same type.
 * </p>
 */
public interface AdjustEstimationsUseCase {

    /**
     * Records the actual time spent on a completed task, recalculates the
     * correction factor for that task type, and applies it to all pending
     * TODO tasks of the same type.
     *
     * @param studentId the authenticated student's identifier
     * @param request   completed-task data including actual time and task type
     * @return adjustment result with the factor and updated estimates
     */
    AdjustEstimationsResponse adjustEstimations(String studentId, AdjustEstimationsRequest request);
}

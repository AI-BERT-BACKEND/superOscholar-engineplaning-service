package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.risk.HighRiskTaskResult;

/**
 * Input port for high-risk task detection (AIB-22.3).
 */
public interface DetectHighRiskTasksUseCase {

    /**
     * Detects tasks where available time until deadline is insufficient
     * relative to the estimated duration.
     *
     * @param studentId The ID of the student
     * @return Result containing at-risk tasks and a summary
     */
    HighRiskTaskResult detectHighRiskTasks(String studentId);
}

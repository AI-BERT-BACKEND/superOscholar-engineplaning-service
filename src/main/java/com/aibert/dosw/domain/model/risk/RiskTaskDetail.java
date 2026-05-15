package com.aibert.dosw.domain.model.risk;

import lombok.Builder;
import lombok.Getter;

/**
 * Detail of a single task identified as at-risk (AIB-22.3).
 */
@Getter
@Builder
public class RiskTaskDetail {

    private final String taskId;
    private final String title;
    private final RiskLevel riskLevel;
    private final int availableMinutes;
    private final int estimatedDurationMinutes;
    private final double academicWeight;
}

package com.aibert.dosw.domain.model.risk;

import lombok.Builder;
import lombok.Getter;

/**
 * Summary of the overall academic risk for a student (AIB-22.3).
 */
@Getter
@Builder
public class RiskSummary {

    /** Number of tasks at risk (HIGH + MEDIUM). */
    private final int totalAtRisk;

    /** Percentage of the total active workload affected by risk. */
    private final double affectedLoadPercent;
}

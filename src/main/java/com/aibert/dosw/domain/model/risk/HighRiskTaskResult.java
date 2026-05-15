package com.aibert.dosw.domain.model.risk;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Aggregated result of the high-risk task detection (AIB-22.3).
 */
@Getter
@Builder
public class HighRiskTaskResult {

    /** Tasks with HIGH or MEDIUM risk, sorted by academicWeight desc. */
    private final List<RiskTaskDetail> highRiskTasks;

    /** Overall risk summary for the student. */
    private final RiskSummary riskSummary;

    /** Human-readable message. */
    private final String message;
}

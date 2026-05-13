package com.aibert.dosw.domain.model.balance;

import com.aibert.dosw.domain.model.balance.DifferentialBalance;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Unified result of the workload balance analysis for a week (R15).
 * Contains the complete output contract: daily analysis, overloaded/empty days,
 * redistribution suggestions, and a human-readable message.
 */
@Getter
@Builder
public class BalanceResult {

    /**
     * Per-day balance data: hours assigned, available, and % occupancy.
     * Corresponds to weeklyLoadAnalysis in the R15 spec.
     */
    private final List<DifferentialBalance> weeklyLoadAnalysis;

    /**
     * Days where the load exceeds 80% of declared availability (RN-01).
     * Contains the date as string (e.g. "2026-05-13" or "LUNES 13-May").
     */
    private final List<String> overloadedDays;

    /**
     * Days where the load is below 20% of declared availability (RN-02).
     */
    private final List<String> emptyDays;

    /**
     * Suggestions: which tasks to move and towards which days.
     */
    private final List<BalanceSuggestion> balanceSuggestions;

    /**
     * Human-readable result message per R15 spec:
     * "La semana está bien distribuida" or
     * "Se detectaron días con sobrecarga, se sugiere redistribuir"
     */
    private final String message;
}

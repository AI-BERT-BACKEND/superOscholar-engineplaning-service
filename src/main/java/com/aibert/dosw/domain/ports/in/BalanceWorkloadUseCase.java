package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.balance.BalanceResult;
import java.time.LocalDate;

/**
 * Input port for the Time Balancer engine (R15).
 * Generates analysis and suggestions to balance the workload across the week.
 */
public interface BalanceWorkloadUseCase {

    /**
     * Analyzes the weekly workload and returns the complete balance report.
     * Detects overloaded days (&gt;80%) and empty days (&lt;20%) and suggests task movements.
     *
     * @param studentId     The ID of the student
     * @param weekStartDate The Monday of the week to analyze
     * @return Full balance result with analysis, overloaded/empty days, and suggestions
     */
    BalanceResult suggestBalance(String studentId, LocalDate weekStartDate);
}


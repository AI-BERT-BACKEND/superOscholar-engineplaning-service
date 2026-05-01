package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.balance.BalanceSuggestion;
import java.util.List;

/**
 * Input port for the Time Balancer engine (R15).
 * Generates suggestions to balance the workload across the week.
 */
public interface BalanceWorkloadUseCase {

    /**
     * Analyzes the weekly workload and returns suggestions to balance it.
     * It detects overloaded days (>80%) and empty days (<20%) to move tasks.
     *
     * @param studentId The ID of the student
     * @return A list of suggestions to balance the week
     */
    List<BalanceSuggestion> suggestBalance(String studentId);
}

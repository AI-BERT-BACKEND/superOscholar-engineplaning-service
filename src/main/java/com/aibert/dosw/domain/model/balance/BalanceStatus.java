package com.aibert.dosw.domain.model.balance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Status of the workload balance for a day.
 */
@Getter
@RequiredArgsConstructor
public enum BalanceStatus {

    OVERLOADED("Overloaded",
            "More tasks assigned than available hours"),
    BALANCED("Balanced",
            "Manageable workload"),
    FREE("Free",
            "Free time available with no assigned tasks");

    private final String displayName;
    private final String description;
}

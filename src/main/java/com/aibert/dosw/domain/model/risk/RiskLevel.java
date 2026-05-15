package com.aibert.dosw.domain.model.risk;

/**
 * Risk level for a task based on available time vs estimated duration (AIB-22.3).
 * <ul>
 *   <li>{@code HIGH} — availableMinutes &lt; 70% of estimatedMinutes</li>
 *   <li>{@code MEDIUM} — availableMinutes between 70% and 85% of estimatedMinutes</li>
 *   <li>{@code NONE} — sufficient time available</li>
 * </ul>
 */
public enum RiskLevel {
    HIGH,
    MEDIUM,
    NONE
}

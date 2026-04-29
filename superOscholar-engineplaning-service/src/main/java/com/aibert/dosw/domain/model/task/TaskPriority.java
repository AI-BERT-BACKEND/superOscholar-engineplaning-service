package com.aibert.dosw.domain.model.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Priority levels for an academic task.
 * Each level has an associated minimum score.
 */
@Getter
@RequiredArgsConstructor
public enum TaskPriority {
    CRITICAL(75.0, "Critical - Immediate attention"),
    HIGH(50.0, "High - Next in line"),
    MEDIUM(25.0, "Medium - Can wait a bit"),
    LOW(0.0, "Low - No immediate urgency");

    private final double minimumScore;
    private final String description;
}

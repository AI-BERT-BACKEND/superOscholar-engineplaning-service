package com.aibert.dosw.domain.model.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Priority levels for an academic task (AIB-22).
 *
 * <ul>
 *   <li>CRITICAL — deadline &lt; 24 h; escalated automatically (RN-02)</li>
 *   <li>HIGH     — score ≥ 70</li>
 *   <li>MEDIUM   — score 40–69</li>
 *   <li>LOW      — score &lt; 40 (also assigned when no deadline)</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum TaskPriority {

    /** Deadline within 24 h — automatically escalated (RN-02). Score = 100. */
    CRITICAL(70.0),
    /** Score ≥ 70. */
    HIGH(70.0),
    /** Score 40–69. */
    MEDIUM(40.0),
    /** Score &lt; 40 or no deadline. */
    LOW(0.0);

    private final double minimumScore;
}

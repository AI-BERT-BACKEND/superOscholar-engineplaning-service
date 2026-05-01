package com.aibert.dosw.application.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing a prioritized task in the API response.
 * Aligned with the task-service JSON schema (studentId, subjectId, deadline, etc).
 */
@Getter
@Builder
public class PrioritizedTaskResponse {
    private final String taskId;
    private final String title;
    private final String subjectId;
    private final LocalDateTime deadline;
    private final int estimatedDurationMinutes;
    private final double priorityScore;
    private final String priorityLevel;
}

package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing a prioritized task in the API response.
 * Aligned with the task-service JSON schema (studentId, subjectId, deadline,
 * etc).
 */
@Getter
@Builder
@Schema(description = "Task details enriched with a computed priority score")
public class PrioritizedTaskResponse {
    @Schema(description = "Task identifier", example = "task-456")
    private final String taskId;

    @Schema(description = "Task title", example = "Linear Algebra Homework")
    private final String title;

    @Schema(description = "Subject identifier", example = "subject-789")
    private final String subjectId;

    @Schema(description = "Task type", example = "TAREA")
    private final String taskType;

    @Schema(description = "Task deadline in ISO-8601 format", example = "2026-05-12T17:00:00")
    private final LocalDateTime deadline;

    @Schema(description = "Scheduled date in ISO-8601 format", example = "2026-05-11T08:00:00")
    private final LocalDateTime scheduledDate;

    @Schema(description = "Estimated duration in minutes", example = "90")
    private final int estimatedDurationMinutes;

    @Schema(description = "Task status", example = "TODO")
    private final String status;

    @Schema(description = "Priority score computed by the engine", example = "0.82")
    private final double priorityScore;

    @Schema(description = "Priority level label", example = "HIGH")
    private final String priorityLevel;
}

package com.aibert.dosw.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "A pre-computed prioritized task candidate to be evaluated for critical recommendations")
public class CriticalTaskCandidateRequest {

    @Schema(description = "Task identifier", example = "task-abc-123")
    private String taskId;

    @Schema(description = "Task title", example = "Linear Algebra Midterm")
    private String title;

    @Schema(description = "Subject identifier", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
    private String subjectId;

    @Schema(description = "Task type: TAREA | EXAMEN | PROYECTO | LECTURA | OTRO", example = "EXAMEN")
    private String taskType;

    @Schema(description = "Task deadline in ISO-8601 format", example = "2026-05-24T17:00:00")
    private LocalDateTime deadline;

    @Schema(description = "Scheduled start date-time in ISO-8601 format", example = "2026-05-23T09:00:00")
    private LocalDateTime scheduledDate;

    @Schema(description = "Estimated duration to complete the task, in minutes", example = "120")
    private Integer estimatedDurationMinutes;

    @Schema(description = "Pre-computed priority score (0–100)", example = "87.5")
    private Double priorityScore;

    @Schema(description = "Priority level label: LOW | MEDIUM | HIGH | CRITICAL", example = "HIGH")
    private String priorityLevel;

    @Schema(description = "Current task status: TODO | IN_PROGRESS | COMPLETED", example = "TODO")
    private String status;
}

package com.aibert.dosw.application.dto.request;

import com.aibert.dosw.domain.model.task.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for AIB-22.4: automatic estimation adjustment.
 * Sent when a student marks a task as COMPLETED and provides the actual time spent.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Data submitted when a student completes a task to trigger automatic estimation adjustment")
public class AdjustEstimationsRequest {

    @NotBlank
    @Schema(description = "Identifier of the recently completed task", example = "task-abc-123")
    private String completedTaskId;

    @NotNull
    @Min(1)
    @Schema(description = "Actual time spent completing the task, in minutes (must be > 0)", example = "95")
    private Integer actualTime;

    @NotNull
    @Min(1)
    @Schema(description = "Original estimated duration of the completed task in minutes (used to compute the correction ratio)", example = "60")
    private Integer estimatedDurationMinutes;

    @NotNull
    @Schema(description = "Task type used to segment the correction factor (TAREA | EXAMEN | PROYECTO | LECTURA | OTRO)", example = "TAREA")
    private TaskType taskType;
}

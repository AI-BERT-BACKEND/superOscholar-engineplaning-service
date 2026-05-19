package com.aibert.dosw.application.dto.request;

import com.aibert.dosw.domain.model.task.TaskChangeEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for receiving task change notifications
 * from the task-service (AIB-22.1).
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Payload sent by the task-service to notify a task change event and trigger automatic prioritization")
public class TaskChangeNotificationRequest {

    @NotBlank
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "studentId must be a valid UUID")
    @Schema(description = "Student identifier whose tasks need to be reprioritized", example = "100095379")
    private String studentId;

    @NotBlank
    @Schema(description = "Identifier of the task that changed", example = "task-456")
    private String taskId;

    @NotNull
    @Schema(description = "Type of change that triggered this notification", allowableValues = { "NUEVA_TAREA",
            "EDICION", "COMPLETADO" })
    private TaskChangeEventType eventType;
}

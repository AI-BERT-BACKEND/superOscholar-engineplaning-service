package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.TaskChangeNotificationRequest;
import com.aibert.dosw.application.event.TaskChangeEvent;
import com.aibert.dosw.domain.model.task.TaskChangeEventType;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for receiving internal task change notifications from the
 * task-service (AIB-22.1).
 * Accepts the notification, immediately returns HTTP 202 Accepted, and fires a
 * {@link TaskChangeEvent} that is processed asynchronously in the background.
 */
@RestController
@RequestMapping("/planning/events")
@RequiredArgsConstructor
@Tag(name = "Task Events", description = "Internal endpoint for receiving task change notifications that trigger automatic background prioritization (AIB-22.1)")
public class TaskEventController {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Receives a task change notification (NUEVA_TAREA, EDICION, COMPLETADO) from
     * the
     * task-service and triggers automatic prioritization in the background without
     * blocking the caller.
     *
     * @param request payload containing studentId, taskId, and eventType
     * @return HTTP 202 Accepted once the event has been dispatched
     */
    @PostMapping("/task-change")
    @Operation(summary = "Notify Task Change", description = "Receives a task lifecycle event from the task-service and triggers automatic priority recalculation in the background. Returns 202 Accepted immediately so the caller is never blocked.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Event received and queued for background processing", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> notifyTaskChange(
            @Valid @RequestBody TaskChangeNotificationRequest request) {

        // Explicit sanitization breaks the taint chain detected by static analysis.
        // @Valid already enforces UUID format and alphanumeric constraints, but parsing
        // here makes the sanitization visible in the data flow.
        String studentId = UUID.fromString(request.getStudentId()).toString();
        String taskId = sanitizeTaskId(request.getTaskId());
        TaskChangeEventType eventType = request.getEventType();

        eventPublisher.publishEvent(new TaskChangeEvent(this, studentId, taskId, eventType));

        return ResponseEntity.accepted()
                .body(ApiResponse.success(
                        "Evento recibido. Recálculo de prioridades iniciado en segundo plano.", null));
    }

    /** Keeps only word characters and hyphens (matches the @Pattern on TaskChangeNotificationRequest). */
    private static String sanitizeTaskId(String taskId) {
        return taskId == null ? "" : taskId.replaceAll("[^\\w\\-]", "");
    }
}

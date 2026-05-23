package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.AdjustEstimationsRequest;
import com.aibert.dosw.application.dto.response.AdjustEstimationsResponse;
import com.aibert.dosw.domain.ports.in.AdjustEstimationsUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for AIB-22.4: automatic estimation adjustment.
 * Records the actual time spent on a completed task, updates the per-student
 * correction factor, and applies it to all pending tasks of the same type.
 */
@Slf4j
@RestController
@RequestMapping("/planning/estimations")
@RequiredArgsConstructor
@Tag(name = "Estimation Adjustment", description = "Manage estimation adjustment: learn from completed tasks and automatically update pending task duration estimates using a per-student correction factor. (AIB-22.4)")
public class AdjustEstimationsController {

    private final AdjustEstimationsUseCase adjustEstimationsUseCase;

    /**
     * Receives the actual time taken to complete a task and, when enough history
     * is available (≥ 5 samples of the same task type), recalculates a personalised
     * correction factor and updates the estimated duration of all pending TODO
     * tasks
     * of that type.
     *
     * @param studentId      student identifier
     * @param authentication current authenticated principal
     * @param request        completed-task data including actual time and task type
     * @return adjustment result with the factor and list of updated task estimates
     */
    @PostMapping("/adjust")
    @Operation(summary = "Adjust Task Estimations", description = "Records the actual time spent on a completed task. Once at least 5 completed tasks of the same type are tracked for the student, a correction factor (avg of actual/estimated ratios, clamped to [0.5, 2.0]) is computed and applied to all pending TODO tasks of that type. Returns the factor and the updated estimates.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estimation adjustment completed (may return 'not enough data' message if fewer than 5 samples exist)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<ApiResponse<AdjustEstimationsResponse>> adjustEstimations(
            Authentication authentication,
            @Valid @RequestBody AdjustEstimationsRequest request) {

        String studentId = authentication.getName();
        log.info("Solicitud de ajuste de estimaciones recibida para el estudiante '{}', tipo de tarea: {}",
                sl(studentId), request.getTaskType());

        AdjustEstimationsResponse result = adjustEstimationsUseCase.adjustEstimations(studentId, request);

        log.info("Ajuste completado para '{}': factor={}, tareas actualizadas={}",
                sl(studentId), result.getAdjustmentFactor(), result.getUpdatedEstimates().size());

        return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static String sl(String s) {
        return s == null ? "" : s.replaceAll("[\r\n]", "_");
    }
}

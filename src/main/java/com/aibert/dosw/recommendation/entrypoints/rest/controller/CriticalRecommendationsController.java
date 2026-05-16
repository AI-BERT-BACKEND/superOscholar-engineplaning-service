package com.aibert.dosw.recommendation.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.CriticalTaskCandidateRequest;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.recommendation.application.dto.request.CriticalRecommendationsRequest;
import com.aibert.dosw.recommendation.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.recommendation.domain.ports.in.CriticalRecommendationsUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for AIB-22.2: critical task recommendations.
 * Dedicated endpoint in the recommendation bounded context.
 */
@Slf4j
@RestController
@RequestMapping("/planning/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Endpoints for retrieving critical task recommendations based on priority, status and deadline proximity")
public class CriticalRecommendationsController {

    private final CriticalRecommendationsUseCase criticalRecommendationsUseCase;

    /**
     * Returns up to 3 critical task recommendations for the authenticated student.
     * Accepts an optional pre-computed task list; when omitted the engine
     * prioritizes internally.
     *
     * @param studentId        student identifier
     * @param request          optional body with a pre-computed ordered task list
     * @param forceRecalculate forces fresh priority recalculation when no list is
     *                         provided
     * @param forzarRecalculo  alias for forceRecalculate (Spanish)
     * @param authentication   current authenticated principal
     * @return up to 3 critical recommendations
     */
    @PostMapping("/critical")
    @Operation(summary = "Get Critical Task Recommendations", description = "Filters prioritized tasks and returns up to 3 critical recommendations (HIGH/CRITICAL priority, active status, deadline within 48 hours). Provide a pre-computed task list via the request body or leave it empty to trigger automatic prioritization.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Critical recommendations retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> getCriticalRecommendations(
            @Parameter(description = "Student identifier", required = true, example = "student-123") @RequestHeader("X-Student-Id") String studentId,
            @RequestBody(required = false) CriticalRecommendationsRequest request,
            @Parameter(description = "Forces recalculation of priority scores when no task list is supplied", example = "false") @RequestParam(name = "forceRecalculate", required = false) Boolean forceRecalculate,
            @RequestParam(name = "forzarRecalculo", required = false) Boolean forzarRecalculo,
            Authentication authentication) {

        assertStudentIdMatchesAuthenticatedUser(authentication, studentId);
        log.info("Solicitud de recomendaciones críticas recibida para el estudiante '{}'", studentId);

        boolean shouldRecalculate = Boolean.TRUE.equals(forceRecalculate)
                || Boolean.TRUE.equals(forzarRecalculo);

        List<PrioritizedTaskResponse> orderedTasks = resolveOrderedTasks(request);

        CriticalRecommendationsResponse response = criticalRecommendationsUseCase
                .getRecommendations(studentId, orderedTasks, shouldRecalculate);

        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private List<PrioritizedTaskResponse> resolveOrderedTasks(CriticalRecommendationsRequest request) {
        if (request == null || request.getOrderedTasks() == null || request.getOrderedTasks().isEmpty()) {
            return null;
        }
        return request.getOrderedTasks().stream()
                .map(this::toPrioritizedResponse)
                .toList();
    }

    private PrioritizedTaskResponse toPrioritizedResponse(CriticalTaskCandidateRequest candidate) {
        String safeTaskId = candidate.getTaskId() != null ? candidate.getTaskId() : "";
        String safeTitle = candidate.getTitle() != null ? candidate.getTitle() : "";
        String safeSubjectId = candidate.getSubjectId() != null ? candidate.getSubjectId() : "";
        String safeTaskType = candidate.getTaskType() != null ? candidate.getTaskType() : "OTRO";
        String safePriorityLevel = candidate.getPriorityLevel() != null ? candidate.getPriorityLevel() : "LOW";
        String safeStatus = candidate.getStatus() != null ? candidate.getStatus() : "TODO";

        return PrioritizedTaskResponse.builder()
                .id(safeTaskId)
                .taskId(safeTaskId)
                .title(safeTitle)
                .subjectId(safeSubjectId)
                .taskType(safeTaskType)
                .deadline(candidate.getDeadline())
                .scheduledDate(candidate.getScheduledDate())
                .estimatedDurationMinutes(candidate.getEstimatedDurationMinutes() != null
                        ? candidate.getEstimatedDurationMinutes()
                        : 0)
                .priorityScore(normalizePriorityScore(candidate.getPriorityScore()))
                .priorityLevel(safePriorityLevel)
                .priority(safePriorityLevel)
                .status(safeStatus)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    private int normalizePriorityScore(Double score) {
        if (score == null) {
            return 0;
        }
        int rounded = (int) Math.round(score);
        if (rounded < 0) {
            return 0;
        }
        return Math.min(rounded, 100);
    }

    private void assertStudentIdMatchesAuthenticatedUser(Authentication authentication, String studentId) {
        if (authentication == null || !StringUtils.hasText(authentication.getName())
                || !authentication.getName().equals(studentId)) {
            throw new AccessDeniedException("El studentId no coincide con el usuario autenticado");
        }
    }
}

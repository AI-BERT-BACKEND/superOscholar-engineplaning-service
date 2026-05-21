package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.CriticalRecommendationsRequest;
import com.aibert.dosw.application.dto.request.CriticalTaskCandidateRequest;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.recommendation.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.recommendation.domain.ports.in.CriticalRecommendationsUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.aibert.dosw.entrypoints.support.StudentIdValidator;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller providing endpoints for task prioritization.
 */
@RestController
@RequestMapping("/planning/prioritization")
@RequiredArgsConstructor
@Tag(name = "Prioritization", description = "Endpoints for retrieving and managing prioritized lists of study tasks based on calculated priority scores")
public class PrioritizationController {

        private final PrioritizeTasksUseCase prioritizeTasksUseCase;
        private final PlanningTaskMapper planningTaskMapper;
        private final CriticalRecommendationsUseCase criticalRecommendationsUseCase;

        /**
         * Exposes a REST endpoint that returns active tasks ordered by
         * priority level (desc) and deadline (asc).
         * 
         * @param studentId        The ID of the student requesting the prioritization
         * @param forceRecalculate If true, forces the engine to recalculate priority
         *                         scores
         * @return HTTP 200 OK with the ordered list of prioritized tasks
         */
        @GetMapping
        @Operation(summary = "Get Prioritized Tasks", description = "Retrieves all active study tasks for a student, ordered by priority level and nearest deadline. Optionally forces recalculation for up-to-date rankings.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Prioritized tasks retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
        })
        public ResponseEntity<ApiResponse<List<PrioritizedTaskResponse>>> getPrioritizedTasks(
                        Authentication authentication,
                        @Parameter(description = "Forces recalculation of priority scores instead of using cached values", example = "false") @RequestParam(name = "forceRecalculate", required = false) Boolean forceRecalculate,
                        @RequestParam(name = "forzarRecalculo", required = false) Boolean forzarRecalculo) {

                String studentId = authentication.getName();

                // 1. Execute the use case
                boolean shouldRecalculate = Boolean.TRUE.equals(forceRecalculate)
                                || Boolean.TRUE.equals(forzarRecalculo);
                var tasks = prioritizeTasksUseCase.prioritize(studentId, shouldRecalculate);

                // 2. Map domain models to DTOs
                List<PrioritizedTaskResponse> responseList = tasks.stream()
                                .map(planningTaskMapper::toPrioritizedResponse)
                                .toList();

                // 3. Return standardized API response
                String message = tasks.isEmpty()
                                ? "No hay tareas activas para priorizar"
                                : "¡Tareas priorizadas exitosamente!";

                return ResponseEntity.ok(
                                ApiResponse.success(message, responseList));
        }

        @PostMapping("/critical")
        @Operation(summary = "Get Critical Task Recommendations", description = "Filters prioritized tasks and returns up to 3 critical recommendations (HIGH/CRITICAL within 48 hours).")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Critical recommendations retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
        })
        public ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> getCriticalRecommendations(
                        Authentication authentication,
                        @RequestBody(required = false) CriticalRecommendationsRequest request,
                        @Parameter(description = "Forces recalculation of priority scores instead of using cached values", example = "false") @RequestParam(name = "forceRecalculate", required = false) Boolean forceRecalculate,
                        @RequestParam(name = "forzarRecalculo", required = false) Boolean forzarRecalculo) {

                String studentId = authentication.getName();

                boolean shouldRecalculate = Boolean.TRUE.equals(forceRecalculate)
                                || Boolean.TRUE.equals(forzarRecalculo);
                List<PrioritizedTaskResponse> orderedTasks = resolveOrderedTasks(studentId, request, shouldRecalculate);
                CriticalRecommendationsResponse response = criticalRecommendationsUseCase
                                .getRecommendations(studentId, orderedTasks, shouldRecalculate);

                return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
        }

        private List<PrioritizedTaskResponse> resolveOrderedTasks(
                        String studentId,
                        CriticalRecommendationsRequest request,
                        boolean forceRecalculate) {
                List<CriticalTaskCandidateRequest> candidates = request != null ? request.getOrderedTasks() : null;
                if (candidates == null || candidates.isEmpty()) {
                        // The use case handles internal prioritization when the list is null
                        return null;
                }

                return candidates.stream()
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
                                .lastUpdated(java.time.LocalDateTime.now())
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

}

package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.FailureReportRequest;
import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.RebalanceTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for dynamic rebalancing scenarios (R17).
 */
@RestController
@RequestMapping("/planning/rebalance")
@RequiredArgsConstructor
@Tag(name = "Rebalance", description = "Endpoints for handling dynamic rebalancing of study schedules when failures or changes occur")
public class RebalanceController {

        private final RebalanceTasksUseCase rebalanceTasksUseCase;
        private final PlanningTaskMapper planningTaskMapper;

        /**
         * Endpoint for the frontend to report that a study block failed or was missed.
         * Automatically triggers a rebalance and returns the new schedule.
         */
        @PostMapping("/failure")
        @Operation(summary = "Report Study Block Failure", description = "Allows the frontend to report a failed or missed study block. The system automatically rebalances the remaining tasks and returns an updated distribution plan for the week.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rebalance completed and updated weekly plan returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid failure report input"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
        })
        public ResponseEntity<ApiResponse<DistributionPlanResponse>> reportFailure(
                        @Parameter(description = "Failure report details including studentId, taskId, failed date, hours missed, and reason", required = true) @RequestBody FailureReportRequest request,
                        Authentication authentication) {

                assertStudentIdMatchesAuthenticatedUser(authentication, request.getStudentId());

                var newPlan = rebalanceTasksUseCase.reportFailureAndRebalance(
                                request.getStudentId(),
                                request.getTaskId(),
                                request.getFailedDate(),
                                request.getHoursMissed(),
                                request.getReason());

                return ResponseEntity.ok(
                                ApiResponse.success("Failure reported and schedule rebalanced successfully",
                                                planningTaskMapper.toDistributionPlanResponse(newPlan)));
        }

        /**
         * Endpoint to explicitly reorganize the remaining pending tasks
         * into the remaining time of the week.
         */
        @PostMapping("/reorganize")
        @Operation(summary = "Reorganize Weekly Schedule", description = "Explicitly reorganizes all pending tasks into the remaining available time slots for the current week, optimizing the study schedule.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Weekly schedule reorganized successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid studentId"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
        })
        public ResponseEntity<ApiResponse<DistributionPlanResponse>> reorganize(
                        @Parameter(description = "Student identifier used to reorganize the remaining weekly schedule", required = true, example = "student-123") @RequestParam String studentId,
                        Authentication authentication) {

                assertStudentIdMatchesAuthenticatedUser(authentication, studentId);

                var updatedPlan = rebalanceTasksUseCase.reorganizePlan(studentId);

                return ResponseEntity.ok(
                                ApiResponse.success("Weekly schedule reorganized successfully",
                                                planningTaskMapper.toDistributionPlanResponse(updatedPlan)));
        }

        private void assertStudentIdMatchesAuthenticatedUser(
                        Authentication authentication,
                        String studentId) {
                if (authentication == null || !StringUtils.hasText(authentication.getName())
                                || !authentication.getName().equals(studentId)) {
                        throw new AccessDeniedException("studentId does not match authenticated user");
                }
        }
}

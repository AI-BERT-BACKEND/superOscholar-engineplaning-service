package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.DistributeTasksUseCase;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for the task distribution engine endpoints.
 */
@RestController
@RequestMapping("/planning/distribution")
@RequiredArgsConstructor
@Tag(name = "Distribution", description = "Endpoints for generating and managing the automatic distribution of study tasks across available time slots")
public class DistributionController {

    private final DistributeTasksUseCase distributeTasksUseCase;
    private final PlanningTaskMapper planningTaskMapper;

    /**
     * Endpoint to automatically distribute tasks into available time blocks.
     * Returns both assigned blocks and unassigned tasks (sobrantes).
     *
     * @param studentId The ID of the student
     * @return HTTP 200 OK with the full distribution plan
     */
    @PostMapping
    @Operation(summary = "Generate Weekly Task Distribution", description = "Automatically distributes all pending study tasks into available time blocks for the week. Returns a complete distribution plan including assigned time slots and any remaining unassigned tasks.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Weekly distribution plan generated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid studentId"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<ApiResponse<DistributionPlanResponse>> generateDistribution(
            @Parameter(description = "Student identifier used to generate the weekly distribution plan", required = true, example = "student-123") @RequestParam String studentId,
            Authentication authentication) {

        assertStudentIdMatchesAuthenticatedUser(authentication, studentId);

        var distributionPlan = distributeTasksUseCase.distribute(studentId);

        DistributionPlanResponse response = planningTaskMapper.toDistributionPlanResponse(distributionPlan);

        return ResponseEntity.ok(
                ApiResponse.success("Weekly distribution generated successfully", response));
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

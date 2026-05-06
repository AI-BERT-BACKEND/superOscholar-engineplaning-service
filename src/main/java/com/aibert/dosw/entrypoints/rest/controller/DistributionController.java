package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.DistributeTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<DistributionPlanResponse>> generateDistribution(
            @RequestParam String studentId,
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

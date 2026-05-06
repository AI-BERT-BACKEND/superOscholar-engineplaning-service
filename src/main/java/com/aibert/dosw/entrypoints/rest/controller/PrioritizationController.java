package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller providing endpoints for task prioritization.
 */
@RestController
@RequestMapping("/planning/prioritization")
@RequiredArgsConstructor
public class PrioritizationController {

        private final PrioritizeTasksUseCase prioritizeTasksUseCase;
        private final PlanningTaskMapper planningTaskMapper;

        /**
         * Exposes a REST endpoint that returns active tasks ordered descendingly by
         * priority score.
         * 
         * @param studentId        The ID of the student requesting the prioritization
         * @param forceRecalculate If true, forces the engine to recalculate priority
         *                         scores
         * @return HTTP 200 OK with the ordered list of prioritized tasks
         */
        @GetMapping
        public ResponseEntity<ApiResponse<List<PrioritizedTaskResponse>>> getPrioritizedTasks(
                        @RequestParam String studentId,
                        @RequestParam(defaultValue = "false") boolean forceRecalculate,
                        Authentication authentication) {

                assertStudentIdMatchesAuthenticatedUser(authentication, studentId);

                // 1. Execute the use case
                var tasks = prioritizeTasksUseCase.prioritize(studentId, forceRecalculate);

                // 2. Map domain models to DTOs
                List<PrioritizedTaskResponse> responseList = tasks.stream()
                                .map(planningTaskMapper::toPrioritizedResponse)
                                .toList();

                // 3. Return standardized API response
                return ResponseEntity.ok(
                                ApiResponse.success("Tasks prioritized successfully", responseList));
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

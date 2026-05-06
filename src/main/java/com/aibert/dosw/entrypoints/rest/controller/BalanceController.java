package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.BalanceSuggestionResponse;
import com.aibert.dosw.application.dto.response.WorkloadBalanceResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.BalanceWorkloadUseCase;
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
 * REST Controller for workload balance suggestions.
 */
@RestController
@RequestMapping("/planning/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceWorkloadUseCase balanceWorkloadUseCase;
    private final PlanningTaskMapper planningTaskMapper;

    /**
     * Returns balance suggestions to move tasks from overloaded days to free days.
     *
     * @param studentId The ID of the student
     * @return HTTP 200 OK with workload balance suggestions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<WorkloadBalanceResponse>> getBalanceSuggestions(
            @RequestParam String studentId,
            Authentication authentication) {

        assertStudentIdMatchesAuthenticatedUser(authentication, studentId);

        List<BalanceSuggestionResponse> suggestions = balanceWorkloadUseCase.suggestBalance(studentId)
                .stream()
                .map(suggestion -> BalanceSuggestionResponse.builder()
                        .task(planningTaskMapper.toPrioritizedResponse(suggestion.getTaskToMove()))
                        .fromDate(suggestion.getFromDate())
                        .toDate(suggestion.getToDate())
                        .reason(suggestion.getReason())
                        .suggestionMessage(suggestion.getSuggestionMessage())
                        .build())
                .toList();

        WorkloadBalanceResponse response = WorkloadBalanceResponse.builder()
                .studentId(studentId)
                .suggestions(suggestions)
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("Workload balance suggestions generated successfully", response));
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

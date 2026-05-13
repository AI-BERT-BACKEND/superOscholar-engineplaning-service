package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.BalanceSuggestionResponse;
import com.aibert.dosw.application.dto.response.DayBalanceResponse;
import com.aibert.dosw.application.dto.response.WorkloadBalanceResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.model.balance.BalanceResult;
import com.aibert.dosw.domain.model.balance.DifferentialBalance;
import com.aibert.dosw.domain.ports.in.BalanceWorkloadUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for workload balance suggestions (R15).
 */
@RestController
@RequestMapping("/planning/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceWorkloadUseCase balanceWorkloadUseCase;
    private final PlanningTaskMapper planningTaskMapper;

    /**
     * Returns the complete balance analysis for a given week.
     * Includes overloaded/empty days, daily load percentages, and redistribution suggestions.
     *
     * @param studentId     The ID of the student
     * @param weekStartDate The Monday of the week to analyze (defaults to current week's Monday)
     * @return HTTP 200 OK with the full workload balance report
     */
    @GetMapping
    public ResponseEntity<ApiResponse<WorkloadBalanceResponse>> getBalanceSuggestions(
            @RequestParam String studentId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
            Authentication authentication) {

        assertStudentIdMatchesAuthenticatedUser(authentication, studentId);

        // Default to current week's Monday if not provided
        LocalDate effectiveWeekStart = weekStartDate != null
                ? weekStartDate
                : LocalDate.now().with(java.time.DayOfWeek.MONDAY);

        BalanceResult result = balanceWorkloadUseCase.suggestBalance(studentId, effectiveWeekStart);

        // Map domain DifferentialBalance → DayBalanceResponse (R15 weeklyLoadAnalysis)
        List<DayBalanceResponse> weeklyLoadAnalysis = result.getWeeklyLoadAnalysis().stream()
                .map(this::toDayBalanceResponse)
                .toList();

        // Map suggestions
        List<BalanceSuggestionResponse> suggestions = result.getBalanceSuggestions().stream()
                .map(s -> BalanceSuggestionResponse.builder()
                        .task(planningTaskMapper.toPrioritizedResponse(s.getTaskToMove()))
                        .fromDate(s.getFromDate())
                        .toDate(s.getToDate())
                        .reason(s.getReason())
                        .suggestionMessage(s.getSuggestionMessage())
                        .build())
                .toList();

        WorkloadBalanceResponse response = WorkloadBalanceResponse.builder()
                .studentId(studentId)
                .weeklyLoadAnalysis(weeklyLoadAnalysis)
                .overloadedDays(result.getOverloadedDays())
                .emptyDays(result.getEmptyDays())
                .balanceSuggestions(suggestions)
                .message(result.getMessage())
                .build();

        return ResponseEntity.ok(ApiResponse.success(result.getMessage(), response));
    }

    private DayBalanceResponse toDayBalanceResponse(DifferentialBalance balance) {
        double occupancy = balance.getAvailableHours() > 0
                ? (balance.getScheduledHours() / balance.getAvailableHours()) * 100.0
                : 0.0;
        return DayBalanceResponse.builder()
                .date(balance.getDate())
                .availableHours(balance.getAvailableHours())
                .assignedHours(balance.getScheduledHours())
                .occupancyPercent(Math.round(occupancy * 100.0) / 100.0)
                .status(balance.getStatus().name())
                .build();
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


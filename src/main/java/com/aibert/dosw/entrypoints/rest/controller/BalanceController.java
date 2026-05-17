package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.BalanceSuggestionResponse;
import com.aibert.dosw.application.dto.response.DayBalanceResponse;
import com.aibert.dosw.application.dto.response.WorkloadBalanceResponse;
import com.aibert.dosw.domain.model.balance.BalanceResult;
import com.aibert.dosw.domain.model.balance.DifferentialBalance;
import com.aibert.dosw.domain.ports.in.BalanceWorkloadUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for workload balance suggestions (AIB-23 / R15).
 */
@RestController
@RequestMapping("/planning/balance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Balance", description = "Endpoints for analyzing weekly workload balance and receiving suggestions to optimize study time distribution")
public class BalanceController {

        private final BalanceWorkloadUseCase balanceWorkloadUseCase;

        /**
         * Returns the complete balance analysis for a given week.
         * Includes overloaded/empty days, daily load percentages, and redistribution
         * suggestions.
         *
         * @param studentId     The ID of the student
         * @param weekStartDate The Monday of the week to analyze (defaults to current
         *                      week's Monday)
         * @return HTTP 200 OK with the full workload balance report
         */
        @GetMapping
        @Operation(summary = "Get Weekly Workload Balance Analysis", description = "Provides a comprehensive analysis of the student's weekly workload balance, including daily occupancy percentages, overloaded days, empty days, and specific suggestions for redistributing tasks to achieve better balance.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Weekly workload balance analysis returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
        })
        public ResponseEntity<ApiResponse<WorkloadBalanceResponse>> getBalanceSuggestions(
                        @Parameter(description = "Student identifier, provided via X-Student-Id request header", required = true, example = "100095379") @RequestHeader("X-Student-Id") String studentId,
                        @Parameter(description = "Week start date (Monday) in ISO format; defaults to current week if omitted", example = "2026-05-12") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
                        Authentication authentication) {

                assertStudentIdMatchesAuthenticatedUser(authentication, studentId);

                // Default to current week's Monday if not provided
                LocalDate effectiveWeekStart = weekStartDate != null
                                ? weekStartDate
                                : LocalDate.now().with(java.time.DayOfWeek.MONDAY);

                log.info("Solicitud de balance semanal para el estudiante '{}', semana: {}", studentId,
                                effectiveWeekStart);

                BalanceResult result = balanceWorkloadUseCase.suggestBalance(studentId, effectiveWeekStart);

                // Map domain DifferentialBalance → DayBalanceResponse (R15 weeklyLoadAnalysis)
                List<DayBalanceResponse> weeklyLoadAnalysis = result.getWeeklyLoadAnalysis().stream()
                                .map(this::toDayBalanceResponse)
                                .toList();

                // Map suggestions (AIB-23: taskId, taskTitle, fromDay, toDay, reason — max 5
                // via use case)
                List<BalanceSuggestionResponse> suggestions = result.getBalanceSuggestions().stream()
                                .map(s -> BalanceSuggestionResponse.builder()
                                                .taskId(s.getTaskToMove().getId())
                                                .taskTitle(s.getTaskToMove().getTitle())
                                                .fromDay(s.getFromDate())
                                                .toDay(s.getToDate())
                                                .reason(s.getReason())
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

        // Map domain DifferentialBalance → DayBalanceResponse (AIB-23: minutes-based)
        private DayBalanceResponse toDayBalanceResponse(DifferentialBalance balance) {
                int availableMinutes = (int) Math.round(balance.getAvailableHours() * 60);
                int assignedMinutes = (int) Math.round(balance.getScheduledHours() * 60);
                double occupancy = availableMinutes > 0
                                ? (assignedMinutes * 100.0) / availableMinutes
                                : 0.0;
                return DayBalanceResponse.builder()
                                .date(balance.getDate())
                                .availableMinutes(availableMinutes)
                                .assignedMinutes(assignedMinutes)
                                .occupancyPercentage(Math.round(occupancy * 100.0) / 100.0)
                                .status(balance.getStatus().name())
                                .build();
        }

        private void assertStudentIdMatchesAuthenticatedUser(
                        Authentication authentication,
                        String studentId) {
                if (authentication == null || !StringUtils.hasText(authentication.getName())
                                || !authentication.getName().equals(studentId)) {
                        throw new AccessDeniedException("El studentId no coincide con el usuario autenticado");
                }
        }
}

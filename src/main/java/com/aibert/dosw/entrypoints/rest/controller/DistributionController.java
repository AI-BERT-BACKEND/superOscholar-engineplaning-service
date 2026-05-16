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
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for the automatic task distribution engine (AIB-24 / R16).
 */
@RestController
@RequestMapping("/planning/distribution")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Distribution", description = "Endpoints for generating and managing the automatic distribution of study tasks across available time slots")
public class DistributionController {

    private final DistributeTasksUseCase distributeTasksUseCase;
    private final PlanningTaskMapper planningTaskMapper;

    /**
     * Endpoint to automatically distribute tasks into available time blocks
     * (AIB-24).
     * Respects MAX_MINUTES_PER_DAY = 240, applies AIB-22.4 correction factor,
     * and excludes personal/rest blocks (AIB-25 / RN-02).
     *
     * @param studentId     Student identifier from X-Student-Id header
     * @param weekStartDate Monday of the week to distribute (optional, defaults to
     *                      current week)
     * @return HTTP 200 OK with the full distribution plan
     */
    @PostMapping
    @Operation(summary = "Generate Weekly Task Distribution (AIB-24)", description = "Automatically distributes all pending study tasks into available time blocks for the week, "
            +
            "respecting MAX_MINUTES_PER_DAY = 240 and applying priority ordering (CRITICAL/HIGH first). " +
            "Returns assigned blocks and any tasks that could not be scheduled.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Weekly distribution plan generated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<ApiResponse<DistributionPlanResponse>> generateDistribution(
            @Parameter(description = "Student identifier, provided via X-Student-Id request header", required = true, example = "student-123") @RequestHeader("X-Student-Id") String studentId,
            @Parameter(description = "Week start date (Monday) in ISO format; defaults to current week if omitted", example = "2026-05-12") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
            Authentication authentication) {

        assertStudentIdMatchesAuthenticatedUser(authentication, studentId);

        LocalDate effectiveWeekStart = weekStartDate != null
                ? weekStartDate
                : LocalDate.now().with(java.time.DayOfWeek.MONDAY);

        log.info("Solicitud de distribución automática para el estudiante '{}', semana: {}", studentId,
                effectiveWeekStart);

        var distributionPlan = distributeTasksUseCase.distribute(studentId, effectiveWeekStart);

        DistributionPlanResponse response = planningTaskMapper.toDistributionPlanResponse(distributionPlan);

        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
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

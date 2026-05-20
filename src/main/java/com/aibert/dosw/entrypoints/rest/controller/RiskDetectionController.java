package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.HighRiskDetectionResponse;
import com.aibert.dosw.application.dto.response.RiskSummaryResponse;
import com.aibert.dosw.application.dto.response.RiskTaskDetailResponse;
import com.aibert.dosw.domain.model.risk.HighRiskTaskResult;
import com.aibert.dosw.domain.model.risk.RiskTaskDetail;
import com.aibert.dosw.domain.ports.in.DetectHighRiskTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import com.aibert.dosw.entrypoints.support.StudentIdValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for AIB-22.3: high-risk task detection.
 * Identifies tasks where available time is insufficient relative to the
 * estimated work duration, and returns a prioritised risk report.
 */
@Slf4j
@RestController
@RequestMapping("/planning/risk")
@RequiredArgsConstructor
@Tag(name = "Risk Detection", description = "Endpoints for detecting tasks with insufficient available time relative to their estimated duration")
public class RiskDetectionController {

    private final DetectHighRiskTasksUseCase detectHighRiskTasksUseCase;

    /**
     * Returns tasks where available minutes until the deadline are insufficient
     * relative to the estimated duration (HIGH: &lt;70 %, MEDIUM: 70–85 %).
     * Tasks with academicWeight &gt; 30 % are surfaced first in the response
     * (RN-02).
     *
     * @param studentId      student identifier
     * @param authentication current authenticated principal
     * @return risk detection result with high-risk tasks and a summary
     */
    @GetMapping("/high-risk")
    @Operation(summary = "Detect High-Risk Tasks", description = "Analyses the student's active tasks against their configured availability schedule and returns tasks where available time is critically insufficient (HIGH: <70% of estimated duration) or moderately insufficient (MEDIUM: 70–85%). Tasks with academic weight >30% are prioritised in the report (RN-02).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Risk detection completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - studentId does not match authenticated user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<ApiResponse<HighRiskDetectionResponse>> detectHighRiskTasks(
            @Parameter(description = "Student identifier", required = true, example = "100095379") @RequestHeader("X-Student-Id") String studentId,
            Authentication authentication) {

        assertStudentIdMatchesAuthenticatedUser(authentication, studentId);
        log.info("Solicitud de detección de alto riesgo recibida para el estudiante '{}'", sl(studentId));

        HighRiskTaskResult result = detectHighRiskTasksUseCase.detectHighRiskTasks(studentId);
        HighRiskDetectionResponse response = toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private HighRiskDetectionResponse toResponse(HighRiskTaskResult result) {
        List<RiskTaskDetailResponse> taskDetails = result.getHighRiskTasks().stream()
                .map(this::toTaskDetailResponse)
                .toList();

        RiskSummaryResponse summary = RiskSummaryResponse.builder()
                .totalAtRisk(result.getRiskSummary().getTotalAtRisk())
                .affectedLoadPercentage(result.getRiskSummary().getAffectedLoadPercent())
                .build();

        return HighRiskDetectionResponse.builder()
                .highRiskTasks(taskDetails)
                .riskSummary(summary)
                .message(result.getMessage())
                .build();
    }

    private RiskTaskDetailResponse toTaskDetailResponse(RiskTaskDetail detail) {
        return RiskTaskDetailResponse.builder()
                .taskId(detail.getTaskId())
                .title(detail.getTitle())
                .riskLevel(detail.getRiskLevel().name())
                .availableMinutes(detail.getAvailableMinutes())
                .estimatedDurationMinutes(detail.getEstimatedDurationMinutes())
                .academicWeight(detail.getAcademicWeight())
                .build();
    }

    private void assertStudentIdMatchesAuthenticatedUser(Authentication authentication, String studentId) {
        StudentIdValidator.validate(studentId);
        if (authentication == null || !StringUtils.hasText(authentication.getName())
                || !authentication.getName().equals(studentId)) {
            throw new AccessDeniedException("El studentId no coincide con el usuario autenticado");
        }
    }

    private static String sl(String s) {
        return s == null ? "" : s.replaceAll("[\r\n]", "_");
    }
}

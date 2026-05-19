package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.HighRiskDetectionResponse;
import com.aibert.dosw.application.dto.response.RiskSummaryResponse;
import com.aibert.dosw.application.dto.response.RiskTaskDetailResponse;
import com.aibert.dosw.domain.exceptions.PlanningDomainException;
import com.aibert.dosw.domain.model.risk.HighRiskTaskResult;
import com.aibert.dosw.domain.model.risk.RiskLevel;
import com.aibert.dosw.domain.model.risk.RiskSummary;
import com.aibert.dosw.domain.model.risk.RiskTaskDetail;
import com.aibert.dosw.domain.ports.in.DetectHighRiskTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AIB-22.3 — RiskDetectionController.
 */
@ExtendWith(MockitoExtension.class)
class RiskDetectionControllerTest {

    private static final String VALID_STUDENT_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Mock
    private DetectHighRiskTasksUseCase detectHighRiskTasksUseCase;

    @InjectMocks
    private RiskDetectionController controller;

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Authentication authFor(String name) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(name);
        return auth;
    }

    private HighRiskTaskResult emptyResult(String message) {
        return HighRiskTaskResult.builder()
                .highRiskTasks(List.of())
                .riskSummary(RiskSummary.builder().totalAtRisk(0).affectedLoadPercent(0.0).build())
                .message(message)
                .build();
    }

    // -------------------------------------------------------------------------
    // Success scenarios
    // -------------------------------------------------------------------------

    @Test
    void shouldReturn200WhenNoTasksAtRisk() {
        when(detectHighRiskTasksUseCase.detectHighRiskTasks(VALID_STUDENT_ID))
                .thenReturn(emptyResult("No se detectaron tareas en riesgo."));

        ResponseEntity<ApiResponse<HighRiskDetectionResponse>> result = controller.detectHighRiskTasks(VALID_STUDENT_ID,
                authFor(VALID_STUDENT_ID));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getData().getHighRiskTasks().isEmpty());
        assertEquals(0, result.getBody().getData().getRiskSummary().getTotalAtRisk());
    }

    @Test
    void shouldReturn200WithHighRiskTasksWhenDetected() {
        RiskTaskDetail detail = RiskTaskDetail.builder()
                .taskId("task-1")
                .title("Parcial de Álgebra")
                .riskLevel(RiskLevel.HIGH)
                .availableMinutes(50)
                .estimatedDurationMinutes(120)
                .academicWeight(0.40)
                .build();

        HighRiskTaskResult serviceResult = HighRiskTaskResult.builder()
                .highRiskTasks(List.of(detail))
                .riskSummary(RiskSummary.builder().totalAtRisk(1).affectedLoadPercent(40.0).build())
                .message("Se detectaron 1 tarea(s) en riesgo académico.")
                .build();

        when(detectHighRiskTasksUseCase.detectHighRiskTasks(VALID_STUDENT_ID))
                .thenReturn(serviceResult);

        ResponseEntity<ApiResponse<HighRiskDetectionResponse>> result = controller.detectHighRiskTasks(VALID_STUDENT_ID,
                authFor(VALID_STUDENT_ID));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getData().getHighRiskTasks().size());
        assertEquals("task-1", result.getBody().getData().getHighRiskTasks().get(0).getTaskId());
        assertEquals("HIGH", result.getBody().getData().getHighRiskTasks().get(0).getRiskLevel());
        assertEquals(1, result.getBody().getData().getRiskSummary().getTotalAtRisk());
        assertEquals(40.0, result.getBody().getData().getRiskSummary().getAffectedLoadPercentage(), 0.001);
    }

    @Test
    void shouldDelegateTo_detectHighRiskTasksUseCase() {
        when(detectHighRiskTasksUseCase.detectHighRiskTasks(VALID_STUDENT_ID))
                .thenReturn(emptyResult("No se detectaron tareas en riesgo."));

        controller.detectHighRiskTasks(VALID_STUDENT_ID, authFor(VALID_STUDENT_ID));

        verify(detectHighRiskTasksUseCase).detectHighRiskTasks(VALID_STUDENT_ID);
    }

    // -------------------------------------------------------------------------
    // FA-01 — no availability configured
    // -------------------------------------------------------------------------

    @Test
    void shouldReturn200WithFa01MessageWhenNoAvailability() {
        when(detectHighRiskTasksUseCase.detectHighRiskTasks(VALID_STUDENT_ID))
                .thenReturn(emptyResult("Configura tu disponibilidad para activar la detección de riesgos."));

        ResponseEntity<ApiResponse<HighRiskDetectionResponse>> result = controller.detectHighRiskTasks(VALID_STUDENT_ID,
                authFor(VALID_STUDENT_ID));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().getData().getMessage().contains("disponibilidad"));
    }

    // -------------------------------------------------------------------------
    // FA-03 — error message propagation from service
    // -------------------------------------------------------------------------

    @Test
    void shouldReturn200WithFa03MessageWhenServiceReturnsError() {
        when(detectHighRiskTasksUseCase.detectHighRiskTasks(VALID_STUDENT_ID))
                .thenReturn(emptyResult(
                        "No se pudo realizar el calculo de prioridad por favor espere o intente mas tarde"));

        ResponseEntity<ApiResponse<HighRiskDetectionResponse>> result = controller.detectHighRiskTasks(VALID_STUDENT_ID,
                authFor(VALID_STUDENT_ID));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().getData().getMessage()
                .contains("No se pudo realizar el calculo de prioridad"));
    }

    // -------------------------------------------------------------------------
    // Security / authorization
    // -------------------------------------------------------------------------

    @Test
    void shouldThrowAccessDeniedWhenAuthenticationIsNull() {
        assertThrows(AccessDeniedException.class,
                () -> controller.detectHighRiskTasks(VALID_STUDENT_ID, null));
    }

    @Test
    void shouldThrowAccessDeniedWhenStudentIdMismatch() {
        assertThrows(AccessDeniedException.class,
                () -> controller.detectHighRiskTasks(VALID_STUDENT_ID, authFor("different-user")));
    }

    @Test
    void shouldThrowAccessDeniedWhenAuthNameIsEmpty() {
        assertThrows(AccessDeniedException.class,
                () -> controller.detectHighRiskTasks(VALID_STUDENT_ID, authFor("")));
    }

    @Test
    void shouldThrowPlanningDomainExceptionForNonUuidStudentId() {
        // StudentIdValidator throws before getName() is called — no stub needed
        Authentication auth = mock(Authentication.class);
        assertThrows(PlanningDomainException.class,
                () -> controller.detectHighRiskTasks("not-a-uuid", auth));
    }
}

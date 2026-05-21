package com.aibert.dosw.recommendation.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import com.aibert.dosw.recommendation.application.dto.request.CriticalRecommendationsRequest;
import com.aibert.dosw.recommendation.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.recommendation.domain.ports.in.CriticalRecommendationsUseCase;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AIB-22.2 — CriticalRecommendationsController.
 */
@ExtendWith(MockitoExtension.class)
class CriticalRecommendationsControllerTest {

    private static final String VALID_STUDENT_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Mock
    private CriticalRecommendationsUseCase criticalRecommendationsUseCase;

    @Mock
    private com.aibert.dosw.infrastructure.messaging.NotificationKafkaProducer notificationKafkaProducer;

    @InjectMocks
    private CriticalRecommendationsController controller;

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Authentication authFor(String name) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(name);
        return auth;
    }

    private CriticalRecommendationsResponse emptyResponse() {
        return CriticalRecommendationsResponse.builder()
                .criticalRecommendations(List.of())
                .criticalCount(0)
                .message("No tienes tareas críticas en este momento")
                .build();
    }

    // -------------------------------------------------------------------------
    // Success scenarios
    // -------------------------------------------------------------------------

    @Test
    void shouldReturn200WithEmptyRecommendationsWhenNoCriticalTasks() {
        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), isNull(), eq(false)))
                .thenReturn(emptyResponse());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> result = controller.getCriticalRecommendations(
                authFor(VALID_STUDENT_ID), null, false, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(0, result.getBody().getData().getCriticalCount());
        assertEquals("No tienes tareas críticas en este momento", result.getBody().getData().getMessage());
    }

    @Test
    void shouldReturn200WithCriticalRecommendationsWhenTasksExist() {
        PrioritizedTaskResponse criticalTask = PrioritizedTaskResponse.builder()
                .taskId("task-1")
                .priorityLevel("CRITICAL")
                .status("TODO")
                .deadline(LocalDateTime.now().plusHours(5))
                .build();

        CriticalRecommendationsResponse serviceResponse = CriticalRecommendationsResponse.builder()
                .criticalRecommendations(List.of(criticalTask))
                .criticalCount(1)
                .message("Tienes 1 tarea(s) crítica(s) que requieren atención inmediata")
                .build();

        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), isNull(), eq(false)))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> result = controller.getCriticalRecommendations(
                authFor(VALID_STUDENT_ID), null, false, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getData().getCriticalCount());
        assertEquals(1, result.getBody().getData().getCriticalRecommendations().size());
        assertEquals("task-1", result.getBody().getData().getCriticalRecommendations().get(0).getTaskId());
    }

    @Test
    void shouldPassForceRecalculateTrueToUseCase() {
        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), isNull(), eq(true)))
                .thenReturn(emptyResponse());

        controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), null, true, null);

        verify(criticalRecommendationsUseCase).getRecommendations(VALID_STUDENT_ID, null, true);
    }

    @Test
    void shouldResolveForzarRecalculoAliasToTrue() {
        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), isNull(), eq(true)))
                .thenReturn(emptyResponse());

        // forzarRecalculo=true, forceRecalculate=null → should resolve to true
        controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), null, null, true);

        verify(criticalRecommendationsUseCase).getRecommendations(VALID_STUDENT_ID, null, true);
    }

    // -------------------------------------------------------------------------
    // FA-02 — error message propagation
    // -------------------------------------------------------------------------

    @Test
    void shouldReturn200WithErrorMessageWhenServiceReturnsFa02Message() {
        CriticalRecommendationsResponse errorResponse = CriticalRecommendationsResponse.builder()
                .criticalRecommendations(List.of())
                .criticalCount(0)
                .message("No se pudo realizar el calculo de prioridad por favor espere o intente mas tarde")
                .build();

        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), isNull(), eq(false)))
                .thenReturn(errorResponse);

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> result = controller.getCriticalRecommendations(
                authFor(VALID_STUDENT_ID), null, false, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().getData().getMessage().contains("No se pudo realizar el calculo"));
    }

}

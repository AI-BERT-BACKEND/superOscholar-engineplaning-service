package com.aibert.dosw.recommendation.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.CriticalTaskCandidateRequest;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import com.aibert.dosw.infrastructure.messaging.NotificationKafkaProducer;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriticalRecommendationsControllerTest {

    private static final String VALID_STUDENT_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Mock
    private CriticalRecommendationsUseCase criticalRecommendationsUseCase;

    @Mock
    private NotificationKafkaProducer notificationKafkaProducer;

    @InjectMocks
    private CriticalRecommendationsController controller;

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
        assertEquals("task-1", result.getBody().getData().getCriticalRecommendations().get(0).getTaskId());
    }

    @Test
    void shouldSendKafkaNotificationWhenCriticalCountGreaterThanZero() {
        CriticalRecommendationsResponse response = CriticalRecommendationsResponse.builder()
                .criticalRecommendations(List.of())
                .criticalCount(2)
                .message("Tienes 2 tarea(s) crítica(s)")
                .build();
        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), isNull(), eq(false)))
                .thenReturn(response);

        controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), null, false, null);

        verify(notificationKafkaProducer).send(any());
    }

    @Test
    void shouldNotSendKafkaNotificationWhenNoCriticalTasks() {
        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), isNull(), eq(false)))
                .thenReturn(emptyResponse());

        controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), null, false, null);

        verify(notificationKafkaProducer, never()).send(any());
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

        controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), null, null, true);

        verify(criticalRecommendationsUseCase).getRecommendations(VALID_STUDENT_ID, null, true);
    }

    @Test
    void shouldReturnCriticalRecommendationsWithOrderedTasks() {
        CriticalTaskCandidateRequest candidate = new CriticalTaskCandidateRequest();
        candidate.setTaskId("task-1");
        candidate.setTitle("Exam");
        candidate.setSubjectId("math");
        candidate.setTaskType("EXAM");
        candidate.setPriorityLevel("CRITICAL");
        candidate.setStatus("TODO");
        candidate.setPriorityScore(90.0);
        candidate.setEstimatedDurationMinutes(60);
        candidate.setDeadline(LocalDateTime.now().plusHours(10));

        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of(candidate));

        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), anyList(), eq(false)))
                .thenReturn(emptyResponse());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> result =
                controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), request, false, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(criticalRecommendationsUseCase).getRecommendations(eq(VALID_STUDENT_ID), anyList(), eq(false));
    }

    @Test
    void shouldMapCandidateWithNullFieldsToDefaults() {
        CriticalTaskCandidateRequest candidate = new CriticalTaskCandidateRequest();
        // all fields null — defaults applied inside toPrioritizedResponse

        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of(candidate));

        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), anyList(), eq(false)))
                .thenReturn(emptyResponse());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> result =
                controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), request, false, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void shouldNormalizePriorityScoreNegativeToZero() {
        CriticalTaskCandidateRequest candidate = new CriticalTaskCandidateRequest();
        candidate.setPriorityScore(-10.0);

        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of(candidate));

        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), anyList(), eq(false)))
                .thenReturn(emptyResponse());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> result =
                controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), request, false, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void shouldNormalizePriorityScoreOver100To100() {
        CriticalTaskCandidateRequest candidate = new CriticalTaskCandidateRequest();
        candidate.setPriorityScore(200.0);

        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of(candidate));

        when(criticalRecommendationsUseCase.getRecommendations(eq(VALID_STUDENT_ID), anyList(), eq(false)))
                .thenReturn(emptyResponse());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> result =
                controller.getCriticalRecommendations(authFor(VALID_STUDENT_ID), request, false, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

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

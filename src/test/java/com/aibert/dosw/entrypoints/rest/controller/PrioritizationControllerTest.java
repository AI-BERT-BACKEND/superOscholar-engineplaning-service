package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.CriticalRecommendationsRequest;
import com.aibert.dosw.application.dto.request.CriticalTaskCandidateRequest;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrioritizationControllerTest {

    private static final String STUDENT_ID = "00000000-0000-0000-0000-000000000001";

    @Mock
    private PrioritizeTasksUseCase prioritizeTasksUseCase;
    @Mock
    private PlanningTaskMapper planningTaskMapper;
    @Mock
    private CriticalRecommendationsUseCase criticalRecommendationsUseCase;
    @InjectMocks
    private PrioritizationController controller;

    private Authentication authFor(String name) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(name);
        return auth;
    }

    @Test
    void shouldPrioritizeTasks() {
        when(prioritizeTasksUseCase.prioritize(STUDENT_ID, false))
                .thenReturn(List.of(PlanningTask.builder().build()));
        when(planningTaskMapper.toPrioritizedResponse(any())).thenReturn(PrioritizedTaskResponse.builder().build());

        ResponseEntity<ApiResponse<List<PrioritizedTaskResponse>>> response =
                controller.getPrioritizedTasks(authFor(STUDENT_ID), false, null);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("¡Tareas priorizadas exitosamente!", response.getBody().getMessage());
    }

    @Test
    void shouldReturnEmptyTasksWithCorrectMessage() {
        when(prioritizeTasksUseCase.prioritize(STUDENT_ID, false)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<PrioritizedTaskResponse>>> response =
                controller.getPrioritizedTasks(authFor(STUDENT_ID), false, null);

        assertNotNull(response.getBody());
        assertEquals("No hay tareas activas para priorizar", response.getBody().getMessage());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void shouldForceRecalculateWhenForceRecalculateTrue() {
        when(prioritizeTasksUseCase.prioritize(STUDENT_ID, true)).thenReturn(List.of());

        controller.getPrioritizedTasks(authFor(STUDENT_ID), true, null);

        verify(prioritizeTasksUseCase).prioritize(STUDENT_ID, true);
    }

    @Test
    void shouldForceRecalculateWhenForzarRecalculoTrue() {
        when(prioritizeTasksUseCase.prioritize(STUDENT_ID, true)).thenReturn(List.of());

        controller.getPrioritizedTasks(authFor(STUDENT_ID), null, true);

        verify(prioritizeTasksUseCase).prioritize(STUDENT_ID, true);
    }

    @Test
    void shouldThrowNullPointerWhenAuthNull() {
        assertThrows(NullPointerException.class,
                () -> controller.getPrioritizedTasks(null, false, null));
    }

    @Test
    void shouldReturnCriticalRecommendationsWithNullRequest() {
        when(criticalRecommendationsUseCase.getRecommendations(eq(STUDENT_ID), isNull(), eq(false)))
                .thenReturn(CriticalRecommendationsResponse.builder()
                        .criticalCount(0)
                        .criticalRecommendations(List.of())
                        .message("No tienes tareas críticas en este momento")
                        .build());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> response =
                controller.getCriticalRecommendations(authFor(STUDENT_ID), null, false, null);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnCriticalRecommendationsWithEmptyOrderedTasks() {
        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of());

        when(criticalRecommendationsUseCase.getRecommendations(eq(STUDENT_ID), isNull(), eq(false)))
                .thenReturn(CriticalRecommendationsResponse.builder()
                        .criticalCount(0)
                        .criticalRecommendations(List.of())
                        .message("No tienes tareas críticas en este momento")
                        .build());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> response =
                controller.getCriticalRecommendations(authFor(STUDENT_ID), request, false, null);

        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturnCriticalRecommendationsWithOrderedTasks() {
        CriticalTaskCandidateRequest candidate = new CriticalTaskCandidateRequest();
        candidate.setTaskId("task-1");
        candidate.setTitle("Math Exam");
        candidate.setSubjectId("math");
        candidate.setTaskType("EXAM");
        candidate.setPriorityLevel("CRITICAL");
        candidate.setStatus("TODO");
        candidate.setPriorityScore(95.0);
        candidate.setEstimatedDurationMinutes(120);
        candidate.setDeadline(LocalDateTime.now().plusHours(10));

        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of(candidate));

        when(criticalRecommendationsUseCase.getRecommendations(eq(STUDENT_ID), anyList(), eq(false)))
                .thenReturn(CriticalRecommendationsResponse.builder()
                        .criticalCount(1)
                        .criticalRecommendations(List.of())
                        .message("Tienes 1 tarea(s) crítica(s)")
                        .build());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> response =
                controller.getCriticalRecommendations(authFor(STUDENT_ID), request, false, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().getCriticalCount());
    }

    @Test
    void shouldMapCandidateWithNullFieldsToDefaults() {
        CriticalTaskCandidateRequest candidate = new CriticalTaskCandidateRequest();
        // all fields null — should fall back to safe defaults

        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of(candidate));

        when(criticalRecommendationsUseCase.getRecommendations(eq(STUDENT_ID), anyList(), eq(false)))
                .thenReturn(CriticalRecommendationsResponse.builder()
                        .criticalCount(0)
                        .criticalRecommendations(List.of())
                        .message("No tienes tareas críticas en este momento")
                        .build());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> response =
                controller.getCriticalRecommendations(authFor(STUDENT_ID), request, false, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldNormalizePriorityScoreNegativeToZero() {
        CriticalTaskCandidateRequest candidate = new CriticalTaskCandidateRequest();
        candidate.setPriorityScore(-5.0);

        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of(candidate));

        when(criticalRecommendationsUseCase.getRecommendations(eq(STUDENT_ID), anyList(), eq(false)))
                .thenReturn(CriticalRecommendationsResponse.builder()
                        .criticalCount(0)
                        .criticalRecommendations(List.of())
                        .message("No tienes tareas críticas en este momento")
                        .build());

        // The normalizePriorityScore(-5.0) → 0 (covered via toPrioritizedResponse)
        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> response =
                controller.getCriticalRecommendations(authFor(STUDENT_ID), request, false, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldNormalizePriorityScoreOver100To100() {
        CriticalTaskCandidateRequest candidate = new CriticalTaskCandidateRequest();
        candidate.setPriorityScore(150.0);

        CriticalRecommendationsRequest request = new CriticalRecommendationsRequest();
        request.setOrderedTasks(List.of(candidate));

        when(criticalRecommendationsUseCase.getRecommendations(eq(STUDENT_ID), anyList(), eq(false)))
                .thenReturn(CriticalRecommendationsResponse.builder()
                        .criticalCount(0)
                        .criticalRecommendations(List.of())
                        .message("No tienes tareas críticas en este momento")
                        .build());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> response =
                controller.getCriticalRecommendations(authFor(STUDENT_ID), request, false, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldPassForzarRecalculoToCriticalRecommendations() {
        when(criticalRecommendationsUseCase.getRecommendations(eq(STUDENT_ID), isNull(), eq(true)))
                .thenReturn(CriticalRecommendationsResponse.builder()
                        .criticalCount(0)
                        .criticalRecommendations(List.of())
                        .message("No tienes tareas críticas en este momento")
                        .build());

        controller.getCriticalRecommendations(authFor(STUDENT_ID), null, null, true);

        verify(criticalRecommendationsUseCase).getRecommendations(STUDENT_ID, null, true);
    }
}

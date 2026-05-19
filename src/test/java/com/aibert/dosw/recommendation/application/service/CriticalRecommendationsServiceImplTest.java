package com.aibert.dosw.recommendation.application.service;

import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.recommendation.application.dto.response.CriticalRecommendationsResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AIB-22.2 — CriticalRecommendationsServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class CriticalRecommendationsServiceImplTest {

    @Mock
    private PrioritizeTasksUseCase prioritizeTasksUseCase;

    @Mock
    private PlanningTaskMapper planningTaskMapper;

    private CriticalRecommendationsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CriticalRecommendationsServiceImpl(prioritizeTasksUseCase, planningTaskMapper);
    }

    // -------------------------------------------------------------------------
    // Helper builder
    // -------------------------------------------------------------------------

    private PrioritizedTaskResponse task(String id, String priority, String status, int hoursUntilDeadline) {
        return PrioritizedTaskResponse.builder()
                .taskId(id)
                .priorityLevel(priority)
                .priority(priority)
                .status(status)
                .deadline(LocalDateTime.now().plusHours(hoursUntilDeadline))
                .build();
    }

    // -------------------------------------------------------------------------
    // Basic filtering
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnCriticalAndHighTasksWithinWindow() {
        List<PrioritizedTaskResponse> input = List.of(
                task("t1", "CRITICAL", "TODO", 6),
                task("t2", "HIGH", "IN_PROGRESS", 20),
                task("t3", "MEDIUM", "TODO", 10) // excluded: medium priority
        );

        CriticalRecommendationsResponse response = service.getRecommendations("s1", input, false);

        assertEquals(2, response.getCriticalCount());
        assertEquals(2, response.getCriticalRecommendations().size());
        assertTrue(response.getMessage().startsWith("Tienes"));
    }

    @Test
    void shouldExcludeTasksOutsideThe48hWindow() {
        List<PrioritizedTaskResponse> input = List.of(
                task("t1", "CRITICAL", "TODO", 6),
                task("t2", "HIGH", "TODO", 72) // 72h — outside window
        );

        CriticalRecommendationsResponse response = service.getRecommendations("s1", input, false);

        assertEquals(1, response.getCriticalCount());
        assertEquals("t1", response.getCriticalRecommendations().get(0).getTaskId());
    }

    @Test
    void shouldExcludeCompletedTasks() {
        List<PrioritizedTaskResponse> input = List.of(
                task("t1", "CRITICAL", "DONE", 5), // excluded: completed
                task("t2", "HIGH", "IN_PROGRESS", 10));

        CriticalRecommendationsResponse response = service.getRecommendations("s1", input, false);

        assertEquals(1, response.getCriticalCount());
        assertEquals("t2", response.getCriticalRecommendations().get(0).getTaskId());
    }

    // -------------------------------------------------------------------------
    // criticalCount reflects TOTAL count, not capped count (bug fix)
    // -------------------------------------------------------------------------

    @Test
    void criticalCountReflectsTotalEvenWhenMoreThanThreeTasks() {
        List<PrioritizedTaskResponse> input = List.of(
                task("t1", "CRITICAL", "TODO", 4),
                task("t2", "CRITICAL", "TODO", 8),
                task("t3", "HIGH", "TODO", 12),
                task("t4", "HIGH", "IN_PROGRESS", 16),
                task("t5", "CRITICAL", "TODO", 20));

        CriticalRecommendationsResponse response = service.getRecommendations("s1", input, false);

        assertEquals(5, response.getCriticalCount(),
                "criticalCount debe reflejar el total de tareas críticas, no el máximo mostrado (3)");
        assertEquals(3, response.getCriticalRecommendations().size(),
                "Solo se deben mostrar máximo 3 recomendaciones (RN-02)");
    }

    // -------------------------------------------------------------------------
    // FA-01 — no critical tasks
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnEmptyMessageWhenNoCriticalTasks() {
        List<PrioritizedTaskResponse> input = List.of(
                task("t1", "MEDIUM", "TODO", 10),
                task("t2", "LOW", "TODO", 5));

        CriticalRecommendationsResponse response = service.getRecommendations("s1", input, false);

        assertEquals(0, response.getCriticalCount());
        assertTrue(response.getCriticalRecommendations().isEmpty());
        assertEquals("No tienes tareas críticas en este momento", response.getMessage());
    }

    @Test
    void shouldReturnEmptyMessageWhenTaskListIsEmpty() {
        CriticalRecommendationsResponse response = service.getRecommendations("s1", List.of(), false);

        assertEquals(0, response.getCriticalCount());
        assertEquals("No tienes tareas críticas en este momento", response.getMessage());
    }

    // -------------------------------------------------------------------------
    // FA-02 — error during internal prioritization
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnErrorMessageWhenPrioritizationFails() {
        when(prioritizeTasksUseCase.prioritize("s1", false))
                .thenThrow(new RuntimeException("Service unavailable"));

        CriticalRecommendationsResponse response = service.getRecommendations("s1", null, false);

        assertEquals(0, response.getCriticalCount());
        assertTrue(response.getCriticalRecommendations().isEmpty());
        assertEquals(
                "No se pudo realizar el calculo de prioridad por favor espere o intente mas tarde",
                response.getMessage());
    }

    // -------------------------------------------------------------------------
    // Ordering — CRITICAL before HIGH, then nearest deadline
    // -------------------------------------------------------------------------

    @Test
    void shouldOrderCriticalBeforeHighAndNearestDeadlineFirst() {
        List<PrioritizedTaskResponse> input = List.of(
                task("high-30h", "HIGH", "TODO", 30),
                task("critical-20h", "CRITICAL", "TODO", 20),
                task("critical-5h", "CRITICAL", "TODO", 5));

        CriticalRecommendationsResponse response = service.getRecommendations("s1", input, false);

        List<PrioritizedTaskResponse> recs = response.getCriticalRecommendations();
        // CRITICAL tasks appear before HIGH, and among CRITICAL nearest deadline first
        assertEquals("critical-5h", recs.get(0).getTaskId());
        assertEquals("critical-20h", recs.get(1).getTaskId());
        assertEquals("high-30h", recs.get(2).getTaskId());
    }

    // -------------------------------------------------------------------------
    // message interpolates the real total count
    // -------------------------------------------------------------------------

    @Test
    void messageShouldContainTotalCriticalCount() {
        List<PrioritizedTaskResponse> input = List.of(
                task("t1", "CRITICAL", "TODO", 4),
                task("t2", "CRITICAL", "TODO", 8),
                task("t3", "HIGH", "TODO", 12),
                task("t4", "HIGH", "IN_PROGRESS", 16),
                task("t5", "CRITICAL", "TODO", 20));

        CriticalRecommendationsResponse response = service.getRecommendations("s1", input, false);

        assertTrue(response.getMessage().contains("5"),
                "El mensaje debe contener el conteo total real (5), no el número de recomendaciones mostradas (3)");
    }
}

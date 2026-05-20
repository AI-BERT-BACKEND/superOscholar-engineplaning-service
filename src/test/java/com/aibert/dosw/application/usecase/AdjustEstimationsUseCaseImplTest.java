package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.dto.request.AdjustEstimationsRequest;
import com.aibert.dosw.application.dto.response.AdjustEstimationsResponse;
import com.aibert.dosw.application.service.CorrectionFactorStore;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.model.task.TaskType;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdjustEstimationsUseCaseImplTest {

    @Mock
    private TaskProviderPort taskProviderPort;

    @Mock
    private CorrectionFactorStore correctionFactorStore;

    @InjectMocks
    private AdjustEstimationsUseCaseImpl useCase;

    private static final String STUDENT_ID = "stu-001";
    private static final String TASK_ID = "task-abc-123";

    private AdjustEstimationsRequest validRequest() {
        AdjustEstimationsRequest req = new AdjustEstimationsRequest();
        req.setCompletedTaskId(TASK_ID);
        req.setActualTime(90);
        req.setTaskType(TaskType.TAREA);
        return req;
    }

    // ─── FA-02: actualTime null ──────────────────────────────────────────────

    @Test
    void fa02_shouldSkipWhenActualTimeIsNull() {
        AdjustEstimationsRequest req = new AdjustEstimationsRequest();
        req.setCompletedTaskId(TASK_ID);
        req.setActualTime(null);
        req.setTaskType(TaskType.TAREA);

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, req);

        assertEquals(1.0, result.getAdjustmentFactor());
        assertTrue(result.getUpdatedEstimates().isEmpty());
        assertTrue(result.getMessage().contains("No se registró tiempo real"));
        verifyNoInteractions(taskProviderPort, correctionFactorStore);
    }

    @Test
    void fa02_shouldSkipWhenActualTimeIsZero() {
        AdjustEstimationsRequest req = new AdjustEstimationsRequest();
        req.setCompletedTaskId(TASK_ID);
        req.setActualTime(0);
        req.setTaskType(TaskType.TAREA);

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, req);

        assertEquals(1.0, result.getAdjustmentFactor());
        assertTrue(result.getUpdatedEstimates().isEmpty());
        verifyNoInteractions(taskProviderPort, correctionFactorStore);
    }

    // ─── Task not found in task-service ─────────────────────────────────────

    @Test
    void shouldSkipWhenTaskNotFoundInTaskService() {
        when(taskProviderPort.getTaskById(TASK_ID)).thenReturn(Optional.empty());

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, validRequest());

        assertEquals(1.0, result.getAdjustmentFactor());
        assertTrue(result.getUpdatedEstimates().isEmpty());
        assertTrue(result.getMessage().contains("No se pudo obtener los datos"));
        verify(correctionFactorStore, never()).record(any(), any(), anyDouble());
    }

    @Test
    void shouldSkipWhenTaskHasZeroEstimatedHours() {
        PlanningTask task = PlanningTask.builder().id(TASK_ID).estimatedHours(0.0).build();
        when(taskProviderPort.getTaskById(TASK_ID)).thenReturn(Optional.of(task));

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, validRequest());

        assertEquals(1.0, result.getAdjustmentFactor());
        assertTrue(result.getUpdatedEstimates().isEmpty());
        verify(correctionFactorStore, never()).record(any(), any(), anyDouble());
    }

    // ─── FA-01: not enough samples ───────────────────────────────────────────

    @Test
    void fa01_shouldSkipWhenNotEnoughSamples() {
        PlanningTask completedTask = PlanningTask.builder().id(TASK_ID).estimatedHours(2.0).build();
        when(taskProviderPort.getTaskById(TASK_ID)).thenReturn(Optional.of(completedTask));
        // Only 2 ratios — below MIN_SAMPLES (5)
        when(correctionFactorStore.getRecentRatios(STUDENT_ID, TaskType.TAREA))
                .thenReturn(List.of(1.1, 1.2));

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, validRequest());

        assertEquals(1.0, result.getAdjustmentFactor());
        assertTrue(result.getUpdatedEstimates().isEmpty());
        assertTrue(result.getMessage().contains("suficientes datos"));
        verify(correctionFactorStore).record(eq(STUDENT_ID), eq(TaskType.TAREA), anyDouble());
    }

    // ─── RN-02/03: factor calculation and clamping ───────────────────────────

    @Test
    void shouldAdjustEstimationsWithExactlyMinSamples() {
        PlanningTask completedTask = PlanningTask.builder().id(TASK_ID).estimatedHours(1.5).build();
        when(taskProviderPort.getTaskById(TASK_ID)).thenReturn(Optional.of(completedTask));
        // 5 ratios averaging 1.2 — within [0.5, 2.0]
        when(correctionFactorStore.getRecentRatios(STUDENT_ID, TaskType.TAREA))
                .thenReturn(List.of(1.0, 1.1, 1.2, 1.3, 1.4));

        PlanningTask pending = PlanningTask.builder()
                .id("pending-1").status(TaskStatus.TODO)
                .type(TaskType.TAREA).estimatedHours(1.0)
                .dueDate(LocalDate.now().plusDays(3)).build();
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(pending));

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, validRequest());

        assertEquals(1, result.getUpdatedEstimates().size());
        assertTrue(result.getAdjustmentFactor() >= 0.5 && result.getAdjustmentFactor() <= 2.0);
        assertTrue(result.getMessage().contains("tarea(s)"));
        verify(taskProviderPort).updateTaskPriorities(anyList());
    }

    @Test
    void shouldClampFactorToMaxWhenRatiosAreVeryHigh() {
        PlanningTask completedTask = PlanningTask.builder().id(TASK_ID).estimatedHours(1.0).build();
        when(taskProviderPort.getTaskById(TASK_ID)).thenReturn(Optional.of(completedTask));
        // Ratios averaging 3.0 — should be clamped to 2.0
        when(correctionFactorStore.getRecentRatios(STUDENT_ID, TaskType.TAREA))
                .thenReturn(List.of(3.0, 3.0, 3.0, 3.0, 3.0));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(Collections.emptyList());

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, validRequest());

        assertEquals(AdjustEstimationsUseCaseImpl.MAX_FACTOR, result.getAdjustmentFactor());
    }

    @Test
    void shouldClampFactorToMinWhenRatiosAreVeryLow() {
        PlanningTask completedTask = PlanningTask.builder().id(TASK_ID).estimatedHours(1.0).build();
        when(taskProviderPort.getTaskById(TASK_ID)).thenReturn(Optional.of(completedTask));
        // Ratios averaging 0.1 — should be clamped to 0.5
        when(correctionFactorStore.getRecentRatios(STUDENT_ID, TaskType.TAREA))
                .thenReturn(List.of(0.1, 0.1, 0.1, 0.1, 0.1));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(Collections.emptyList());

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, validRequest());

        assertEquals(AdjustEstimationsUseCaseImpl.MIN_FACTOR, result.getAdjustmentFactor());
    }

    @Test
    void shouldReturnNoTasksMessageWhenNoPendingTasks() {
        PlanningTask completedTask = PlanningTask.builder().id(TASK_ID).estimatedHours(1.0).build();
        when(taskProviderPort.getTaskById(TASK_ID)).thenReturn(Optional.of(completedTask));
        when(correctionFactorStore.getRecentRatios(STUDENT_ID, TaskType.TAREA))
                .thenReturn(List.of(1.0, 1.0, 1.0, 1.0, 1.0));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(Collections.emptyList());

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, validRequest());

        assertTrue(result.getMessage().contains("No hay tareas pendientes"));
        assertTrue(result.getUpdatedEstimates().isEmpty());
        verify(taskProviderPort, never()).updateTaskPriorities(anyList());
    }

    @Test
    void shouldIgnorePendingTasksOfDifferentType() {
        PlanningTask completedTask = PlanningTask.builder().id(TASK_ID).estimatedHours(1.0).build();
        when(taskProviderPort.getTaskById(TASK_ID)).thenReturn(Optional.of(completedTask));
        when(correctionFactorStore.getRecentRatios(STUDENT_ID, TaskType.TAREA))
                .thenReturn(List.of(1.2, 1.2, 1.2, 1.2, 1.2));

        // Pending tasks of a different type — should NOT be adjusted
        PlanningTask differentType = PlanningTask.builder()
                .id("p1").status(TaskStatus.TODO).type(TaskType.EXAMEN)
                .estimatedHours(2.0).build();
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(differentType));

        AdjustEstimationsResponse result = useCase.adjustEstimations(STUDENT_ID, validRequest());

        assertTrue(result.getUpdatedEstimates().isEmpty());
        verify(taskProviderPort, never()).updateTaskPriorities(anyList());
    }
}

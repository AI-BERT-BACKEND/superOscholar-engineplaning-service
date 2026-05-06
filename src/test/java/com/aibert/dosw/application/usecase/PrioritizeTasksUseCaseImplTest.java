package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrioritizeTasksUseCaseImplTest {

    @Mock
    private TaskProviderPort taskProviderPort;

    @InjectMocks
    private PrioritizeTasksUseCaseImpl useCase;

    @Test
    void shouldPrioritizeTasksCorrectly() {
        PlanningTask t1 = PlanningTask.builder()
            .id("1").title("T1").dueDate(LocalDate.now().plusDays(1))
            .subjectCredits(4).estimatedHours(3.0).build();

        PlanningTask t2 = PlanningTask.builder()
            .id("2").title("T2").dueDate(LocalDate.now().plusDays(10))
            .subjectCredits(2).estimatedHours(1.0).build();

        when(taskProviderPort.getPendingTasksByUser("student1")).thenReturn(List.of(t1, t2));

        List<PlanningTask> result = useCase.prioritize("student1", false);

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId()); // T1 should have higher priority due to proximity
        verify(taskProviderPort).updateTaskPriorities(anyList());
    }

    @Test
    void shouldReturnEmptyForNullTasks() {
        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(null);
        List<PlanningTask> result = useCase.prioritize("st1", false);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyForEmptyTasks() {
        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of());
        List<PlanningTask> result = useCase.prioritize("st1", false);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotRecalculateWhenNotForcedAndPriorityExists() {
        PlanningTask task = PlanningTask.builder()
            .id("1").dueDate(LocalDate.now().plusDays(3))
            .priorityLevel(TaskPriority.HIGH).priorityScore(60.0)
            .estimatedHours(2.0).build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(new ArrayList<>(List.of(task)));

        List<PlanningTask> result = useCase.prioritize("st1", false);

        assertEquals(1, result.size());
        // Score should remain unchanged since forceRecalculate is false and priorityLevel is not null
        assertEquals(60.0, result.get(0).getPriorityScore());
        verify(taskProviderPort).updateTaskPriorities(anyList());
    }

    @Test
    void shouldRecalculateWhenForced() {
        PlanningTask task = PlanningTask.builder()
            .id("1").dueDate(LocalDate.now().plusDays(3))
            .priorityLevel(TaskPriority.HIGH).priorityScore(60.0)
            .estimatedHours(2.0).build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(new ArrayList<>(List.of(task)));

        List<PlanningTask> result = useCase.prioritize("st1", true);

        assertEquals(1, result.size());
        // Score should be recalculated since forceRecalculate is true
        assertNotNull(result.get(0).getPriorityLevel());
        verify(taskProviderPort).updateTaskPriorities(anyList());
    }

    @Test
    void shouldHandleNullTaskWeightInGrade() {
        PlanningTask task = PlanningTask.builder()
            .id("1").dueDate(LocalDate.now().plusDays(5))
            .taskWeightInGrade(null)
            .estimatedHours(3.0).build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(new ArrayList<>(List.of(task)));

        List<PlanningTask> result = useCase.prioritize("st1", true);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getPriorityLevel());
    }

    @Test
    void shouldHandleTaskWithWeight() {
        PlanningTask task = PlanningTask.builder()
            .id("1").dueDate(LocalDate.now().plusDays(5))
            .taskWeightInGrade(50.0)
            .estimatedHours(3.0).build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(new ArrayList<>(List.of(task)));

        List<PlanningTask> result = useCase.prioritize("st1", true);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getPriorityLevel());
    }
}

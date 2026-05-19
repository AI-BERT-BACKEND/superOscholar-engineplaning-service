package com.aibert.dosw.application.event;

import com.aibert.dosw.domain.model.task.TaskChangeEventType;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskChangeEventListenerTest {

    private static final String STUDENT_UUID = "00000000-0000-0000-0000-000000000001";

    @Mock
    private PrioritizeTasksUseCase prioritizeTasksUseCase;

    @InjectMocks
    private TaskChangeEventListener listener;

    @Test
    void shouldCallPrioritizeWithForceRecalculateTrueOnNuevaTarea() {
        TaskChangeEvent event = new TaskChangeEvent(
                this, STUDENT_UUID, "task-123", TaskChangeEventType.NUEVA_TAREA);

        listener.onTaskChange(event);

        verify(prioritizeTasksUseCase).prioritize(STUDENT_UUID, true);
    }

    @Test
    void shouldCallPrioritizeWithForceRecalculateTrueOnEdicion() {
        TaskChangeEvent event = new TaskChangeEvent(
                this, STUDENT_UUID, "task-456", TaskChangeEventType.EDICION);

        listener.onTaskChange(event);

        verify(prioritizeTasksUseCase).prioritize(eq(STUDENT_UUID), eq(true));
    }

    @Test
    void shouldCallPrioritizeWithForceRecalculateTrueOnCompletado() {
        TaskChangeEvent event = new TaskChangeEvent(
                this, STUDENT_UUID, "task-789", TaskChangeEventType.COMPLETADO);

        listener.onTaskChange(event);

        verify(prioritizeTasksUseCase).prioritize(eq(STUDENT_UUID), eq(true));
    }

    @Test
    void shouldHandleExceptionGracefully() {
        TaskChangeEvent event = new TaskChangeEvent(
                this, STUDENT_UUID, "task-999", TaskChangeEventType.EDICION);

        doThrow(new RuntimeException("Service unavailable"))
                .when(prioritizeTasksUseCase).prioritize(any(), anyBoolean());

        assertDoesNotThrow(() -> listener.onTaskChange(event));
    }

    @Test
    void shouldNotPropagateExceptionWhenPrioritizationFails() {
        TaskChangeEvent event = new TaskChangeEvent(
                this, "00000000-0000-0000-0000-000000000002", "task-000", TaskChangeEventType.NUEVA_TAREA);

        doThrow(new IllegalStateException("No tasks found"))
                .when(prioritizeTasksUseCase).prioritize(any(), anyBoolean());

        assertDoesNotThrow(() -> listener.onTaskChange(event));
        verify(prioritizeTasksUseCase).prioritize("00000000-0000-0000-0000-000000000002", true);
    }
}

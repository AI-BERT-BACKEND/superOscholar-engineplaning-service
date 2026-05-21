package com.aibert.dosw.application.event;

import com.aibert.dosw.domain.model.task.TaskChangeEventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskChangeEventTest {

    private static final String STUDENT_UUID = "00000000-0000-0000-0000-000000000001";

    @Test
    void shouldStoreAllFieldsWhenConstructedWithValidArgs() {
        TaskChangeEvent event = new TaskChangeEvent(
                this, STUDENT_UUID, "task-1", TaskChangeEventType.NUEVA_TAREA);

        assertEquals(STUDENT_UUID, event.getStudentId());
        assertEquals("task-1", event.getTaskId());
        assertEquals(TaskChangeEventType.NUEVA_TAREA, event.getEventType());
        assertSame(this, event.getSource());
    }

    @Test
    void shouldThrowNullPointerWhenStudentIdIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new TaskChangeEvent(this, null, "task-1", TaskChangeEventType.NUEVA_TAREA));
        assertTrue(ex.getMessage().contains("studentId"), "message should name the null field");
    }

    @Test
    void shouldThrowNullPointerWhenTaskIdIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new TaskChangeEvent(this, STUDENT_UUID, null, TaskChangeEventType.NUEVA_TAREA));
        assertTrue(ex.getMessage().contains("taskId"), "message should name the null field");
    }

    @Test
    void shouldThrowNullPointerWhenEventTypeIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new TaskChangeEvent(this, STUDENT_UUID, "task-1", null));
        assertTrue(ex.getMessage().contains("eventType"), "message should name the null field");
    }

    @Test
    void shouldAcceptAllEventTypes() {
        for (TaskChangeEventType type : TaskChangeEventType.values()) {
            TaskChangeEvent event = new TaskChangeEvent(this, STUDENT_UUID, "task-1", type);
            assertEquals(type, event.getEventType());
        }
    }

    @Test
    void shouldAcceptEmptyStringTaskId() {
        TaskChangeEvent event = new TaskChangeEvent(
                this, STUDENT_UUID, "", TaskChangeEventType.COMPLETADO);
        assertEquals("", event.getTaskId());
    }
}

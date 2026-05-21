package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceClientFallbackTest {

    private final TaskServiceClientFallback fallback = new TaskServiceClientFallback();

    @Test
    void getTasksByStudent_returnsEmptyList() {
        List<TaskServiceResponse> result = fallback.getTasksByStudent("student1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateTaskPriorities_doesNotThrow() {
        List<TaskServiceResponse> tasks = List.of(TaskServiceResponse.builder().id("1").build());
        assertDoesNotThrow(() -> fallback.updateTaskPriorities(tasks));
    }

    @Test
    void reportTaskFailure_doesNotThrow() {
        assertDoesNotThrow(() -> fallback.reportTaskFailure("st1", "t1", 2.0, "reason"));
    }

    @Test
    void getTaskById_returnsNull() {
        TaskServiceResponse result = fallback.getTaskById("task-999");
        assertNull(result);
    }
}

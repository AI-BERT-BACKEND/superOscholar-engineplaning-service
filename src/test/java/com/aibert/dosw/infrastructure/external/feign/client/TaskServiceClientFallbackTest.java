package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceStatusUpdateRequest;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceClientFallbackTest {

    private final TaskServiceClientFallback fallback = new TaskServiceClientFallback();

    @Test
    void getTasksByStudent_returnsEmptyList() {
        List<TaskServiceResponse> result = fallback.getTasksByStudent("student1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void patchTask_doesNotThrow() {
        TaskServiceUpdateRequest request = TaskServiceUpdateRequest.builder()
                .priority("HIGH")
                .estimatedDurationMinutes(90)
                .build();
        assertDoesNotThrow(() -> fallback.patchTask("task-1", request));
    }

    @Test
    void patchTaskStatus_doesNotThrow() {
        TaskServiceStatusUpdateRequest request = TaskServiceStatusUpdateRequest.builder()
                .status("TODO")
                .build();
        assertDoesNotThrow(() -> fallback.patchTaskStatus("task-1", request));
    }

    @Test
    void getTaskById_returnsNull() {
        TaskServiceResponse result = fallback.getTaskById("task-999");
        assertNull(result);
    }
}

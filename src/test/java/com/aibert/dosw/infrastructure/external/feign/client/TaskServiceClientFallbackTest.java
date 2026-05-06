package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceClientFallbackTest {

    private final TaskServiceClientFallback fallback = new TaskServiceClientFallback();

    @Test
    void getPendingTasks_returnsEmptyList() {
        List<PlanningTask> result = fallback.getPendingTasks("student1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getScheduledTasks_returnsEmptyList() {
        List<PlanningTask> result = fallback.getScheduledTasks("student1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateTaskPriorities_doesNotThrow() {
        List<PlanningTask> tasks = List.of(PlanningTask.builder().id("1").build());
        assertDoesNotThrow(() -> fallback.updateTaskPriorities(tasks));
    }

    @Test
    void reportTaskFailure_doesNotThrow() {
        assertDoesNotThrow(() ->
            fallback.reportTaskFailure("st1", "t1", 2.0, "reason"));
    }
}

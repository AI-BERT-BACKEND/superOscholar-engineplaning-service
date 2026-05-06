package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.infrastructure.external.feign.client.TaskServiceClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceAdapterTest {

    @Mock
    private TaskServiceClient taskServiceClient;

    @InjectMocks
    private TaskServiceAdapter adapter;

    @Test
    void shouldGetPendingTasksByUser() {
        PlanningTask task = PlanningTask.builder().id("1").build();
        when(taskServiceClient.getPendingTasks("st1")).thenReturn(List.of(task));

        List<PlanningTask> result = adapter.getPendingTasksByUser("st1");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        verify(taskServiceClient).getPendingTasks("st1");
    }

    @Test
    void shouldGetScheduledTasksByUser() {
        PlanningTask task = PlanningTask.builder().id("2").build();
        when(taskServiceClient.getScheduledTasks("st1")).thenReturn(List.of(task));

        List<PlanningTask> result = adapter.getScheduledTasksByUser("st1");

        assertEquals(1, result.size());
        verify(taskServiceClient).getScheduledTasks("st1");
    }

    @Test
    void shouldUpdateTaskPriorities() {
        List<PlanningTask> tasks = List.of(PlanningTask.builder().id("1").build());

        adapter.updateTaskPriorities(tasks);

        verify(taskServiceClient).updateTaskPriorities(tasks);
    }

    @Test
    void shouldReportTaskFailure() {
        adapter.reportTaskFailure("st1", "t1", 2.0, "reason");

        verify(taskServiceClient).reportTaskFailure("st1", "t1", 2.0, "reason");
    }
}

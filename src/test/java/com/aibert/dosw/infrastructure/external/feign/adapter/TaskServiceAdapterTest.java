package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.infrastructure.external.feign.client.TaskServiceClient;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import com.aibert.dosw.infrastructure.external.feign.mapper.TaskResponseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceAdapterTest {

    @Mock
    private TaskServiceClient taskServiceClient;

    @Mock
    private TaskResponseMapper taskResponseMapper;

    @InjectMocks
    private TaskServiceAdapter adapter;

    @Test
    void shouldGetPendingTasksByUser() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1").studentId("st1").title("Test Task")
                .estimatedDurationMinutes(90)
                .deadline(LocalDateTime.of(2026, 6, 15, 23, 59))
                .priority("HIGH").status("TODO")
                .build();
        PlanningTask expected = PlanningTask.builder()
                .id("1").userId("st1").title("Test Task")
                .estimatedHours(1.5).priorityLevel(TaskPriority.ALTA)
                .status(TaskStatus.PENDING)
                .build();

        when(taskServiceClient.getPendingTasks("st1")).thenReturn(List.of(response));
        when(taskResponseMapper.toPlanningTasks(List.of(response))).thenReturn(List.of(expected));

        List<PlanningTask> result = adapter.getPendingTasksByUser("st1");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("st1", result.get(0).getUserId());
        verify(taskServiceClient).getPendingTasks("st1");
        verify(taskResponseMapper).toPlanningTasks(List.of(response));
    }

    @Test
    void shouldGetScheduledTasksByUser() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("2").studentId("st1")
                .scheduledDate(LocalDateTime.of(2026, 6, 10, 8, 0))
                .status("SCHEDULED")
                .build();
        PlanningTask expected = PlanningTask.builder().id("2").userId("st1").build();

        when(taskServiceClient.getScheduledTasks("st1")).thenReturn(List.of(response));
        when(taskResponseMapper.toPlanningTasks(List.of(response))).thenReturn(List.of(expected));

        List<PlanningTask> result = adapter.getScheduledTasksByUser("st1");

        assertEquals(1, result.size());
        verify(taskServiceClient).getScheduledTasks("st1");
        verify(taskResponseMapper).toPlanningTasks(List.of(response));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldUpdateTaskPriorities() {
        PlanningTask task = PlanningTask.builder()
                .id("1").userId("st1").title("Task")
                .estimatedHours(2.0).priorityLevel(TaskPriority.ALTA)
                .status(TaskStatus.PENDING)
                .build();

        adapter.updateTaskPriorities(List.of(task));

        ArgumentCaptor<List<TaskServiceResponse>> captor = ArgumentCaptor.forClass(List.class);
        verify(taskServiceClient).updateTaskPriorities(captor.capture());

        List<TaskServiceResponse> sent = captor.getValue();
        assertEquals(1, sent.size());
        assertEquals("1", sent.get(0).getId());
        assertEquals("st1", sent.get(0).getStudentId());
        assertEquals(120, sent.get(0).getEstimatedDurationMinutes());
        assertEquals("ALTA", sent.get(0).getPriority());
        assertEquals("TODO", sent.get(0).getStatus()); // PENDING → TODO
    }

    @Test
    void shouldReportTaskFailure() {
        adapter.reportTaskFailure("st1", "t1", 2.0, "reason");

        verify(taskServiceClient).reportTaskFailure("st1", "t1", 2.0, "reason");
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldConvertNullFieldsGracefully() {
        PlanningTask task = PlanningTask.builder().id("1").build();

        adapter.updateTaskPriorities(List.of(task));

        ArgumentCaptor<List<TaskServiceResponse>> captor = ArgumentCaptor.forClass(List.class);
        verify(taskServiceClient).updateTaskPriorities(captor.capture());

        TaskServiceResponse sent = captor.getValue().get(0);
        assertEquals("1", sent.getId());
        assertNull(sent.getDeadline());
        assertNull(sent.getScheduledDate());
        assertNull(sent.getEstimatedDurationMinutes());
        assertEquals("TODO", sent.getStatus()); // null status → TODO
    }
}

package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.infrastructure.external.feign.client.TaskServiceClient;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceStatusUpdateRequest;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceUpdateRequest;
import com.aibert.dosw.infrastructure.external.feign.mapper.TaskResponseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
                .estimatedHours(1.5).priorityLevel(TaskPriority.HIGH)
                .status(TaskStatus.TODO)
                .build();

        when(taskServiceClient.getTasksByStudent("st1")).thenReturn(List.of(response));
        when(taskResponseMapper.toPlanningTasks(List.of(response))).thenReturn(List.of(expected));

        List<PlanningTask> result = adapter.getPendingTasksByUser("st1");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("st1", result.get(0).getUserId());
        verify(taskServiceClient).getTasksByStudent("st1");
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

        when(taskServiceClient.getTasksByStudent("st1")).thenReturn(List.of(response));
        when(taskResponseMapper.toPlanningTasks(List.of(response))).thenReturn(List.of(expected));

        List<PlanningTask> result = adapter.getScheduledTasksByUser("st1");

        assertEquals(1, result.size());
        verify(taskServiceClient).getTasksByStudent("st1");
        verify(taskResponseMapper).toPlanningTasks(List.of(response));
    }

    @Test
    void shouldUpdateTaskPriorities() {
        PlanningTask task = PlanningTask.builder()
                .id("1")
                .estimatedHours(2.0)
                .priorityLevel(TaskPriority.HIGH)
                .build();

        adapter.updateTaskPriorities(List.of(task));

        ArgumentCaptor<TaskServiceUpdateRequest> captor = ArgumentCaptor.forClass(TaskServiceUpdateRequest.class);
        verify(taskServiceClient).patchTask(eq("1"), captor.capture());

        TaskServiceUpdateRequest sent = captor.getValue();
        assertEquals(120, sent.getEstimatedDurationMinutes());
        assertEquals("HIGH", sent.getPriority());
    }

    @Test
    void shouldReportTaskFailure() {
        adapter.reportTaskFailure("st1", "t1", 2.0, "reason");

        ArgumentCaptor<TaskServiceStatusUpdateRequest> captor =
                ArgumentCaptor.forClass(TaskServiceStatusUpdateRequest.class);
        verify(taskServiceClient).patchTaskStatus(eq("t1"), captor.capture());
        assertEquals("TODO", captor.getValue().getStatus());
    }

    @Test
    void shouldConvertNullFieldsGracefully() {
        PlanningTask task = PlanningTask.builder().id("1").build();

        adapter.updateTaskPriorities(List.of(task));

        ArgumentCaptor<TaskServiceUpdateRequest> captor = ArgumentCaptor.forClass(TaskServiceUpdateRequest.class);
        verify(taskServiceClient).patchTask(eq("1"), captor.capture());

        TaskServiceUpdateRequest sent = captor.getValue();
        assertNull(sent.getEstimatedDurationMinutes());
        assertNull(sent.getPriority());
    }

    @Test
    void shouldSkipUpdateWhenTaskIdIsBlank() {
        adapter.updateTaskPriorities(List.of(PlanningTask.builder().id(" ").build()));
        verifyNoInteractions(taskServiceClient);
    }

    @Test
    void shouldSkipReportFailureWhenTaskIdIsBlank() {
        adapter.reportTaskFailure("st1", " ", 1.0, "reason");
        verifyNoInteractions(taskServiceClient);
    }
}

package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.infrastructure.external.feign.client.TaskServiceClient;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceStatusUpdateRequest;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceUpdateRequest;
import com.aibert.dosw.infrastructure.external.feign.mapper.TaskResponseMapper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Secondary adapter that implements {@link TaskProviderPort} using Feign.
 * Calls GET /api/tasks/student/{studentId} and filters by status locally,
 * since task-service does not expose separate /pending or /scheduled endpoints.
 */
@Component
@RequiredArgsConstructor
public class TaskServiceAdapter implements TaskProviderPort {

    private final TaskServiceClient taskServiceClient;
    private final TaskResponseMapper taskResponseMapper;

    @Override
    public List<PlanningTask> getPendingTasksByUser(String studentId) {
        List<TaskServiceResponse> all = taskServiceClient.getTasksByStudent(studentId);
        List<TaskServiceResponse> pending = all.stream()
                .filter(t -> "TODO".equalsIgnoreCase(t.getStatus())
                        || "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
                .toList();
        return taskResponseMapper.toPlanningTasks(pending);
    }

    @Override
    public List<PlanningTask> getScheduledTasksByUser(String studentId) {
        List<TaskServiceResponse> all = taskServiceClient.getTasksByStudent(studentId);
        List<TaskServiceResponse> scheduled = all.stream()
                .filter(t -> "SCHEDULED".equalsIgnoreCase(t.getStatus()))
                .toList();
        return taskResponseMapper.toPlanningTasks(scheduled);
    }

    @Override
    public void updateTaskPriorities(List<PlanningTask> tasks) {
        tasks.stream()
                .filter(Objects::nonNull)
                .filter(task -> task.getId() != null && !task.getId().isBlank())
                .forEach(task -> taskServiceClient.patchTask(task.getId(), toTaskServiceUpdateRequest(task)));
    }

    @Override
    public void reportTaskFailure(String studentId, String taskId, double hoursMissed, String reason) {
        if (taskId == null || taskId.isBlank()) {
            return;
        }
        taskServiceClient.patchTaskStatus(
                taskId,
                TaskServiceStatusUpdateRequest.builder().status("TODO").build());
    }

    @Override
    public Optional<PlanningTask> getTaskById(String taskId) {
        TaskServiceResponse response = taskServiceClient.getTaskById(taskId);
        if (response == null) {
            return Optional.empty();
        }
        return Optional.of(taskResponseMapper.toPlanningTask(response));
    }

    private TaskServiceUpdateRequest toTaskServiceUpdateRequest(PlanningTask task) {
        return TaskServiceUpdateRequest.builder()
                .estimatedDurationMinutes(
                        task.getCorrectedEstimatedMinutes() != null ? task.getCorrectedEstimatedMinutes()
                                : task.getEstimatedHours() > 0 ? (int) (task.getEstimatedHours() * 60) : null)
                .priority(task.getPriorityLevel() != null ? task.getPriorityLevel().name() : null)
                .build();
    }
}

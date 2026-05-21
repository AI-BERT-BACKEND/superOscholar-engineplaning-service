package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.infrastructure.external.feign.client.TaskServiceClient;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import com.aibert.dosw.infrastructure.external.feign.mapper.TaskResponseMapper;
import java.util.List;
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
        List<TaskServiceResponse> responses = tasks.stream()
                .map(this::toTaskServiceResponse)
                .toList();
        taskServiceClient.updateTaskPriorities(responses);
    }

    @Override
    public void reportTaskFailure(String studentId, String taskId, double hoursMissed, String reason) {
        taskServiceClient.reportTaskFailure(studentId, taskId, hoursMissed, reason);
    }

    @Override
    public Optional<PlanningTask> getTaskById(String taskId) {
        TaskServiceResponse response = taskServiceClient.getTaskById(taskId);
        if (response == null) {
            return Optional.empty();
        }
        return Optional.of(taskResponseMapper.toPlanningTask(response));
    }

    private TaskServiceResponse toTaskServiceResponse(PlanningTask task) {
        var deadline = task.getDueDateTime();
        if (deadline == null && task.getDueDate() != null) {
            deadline = task.getDueDate().atTime(23, 59);
        }

        var scheduled = task.getScheduledDateTime();
        if (scheduled == null && task.getScheduledDate() != null) {
            scheduled = task.getScheduledDate().atStartOfDay();
        }

        return TaskServiceResponse.builder()
                .id(task.getId())
                .studentId(task.getUserId())
                .title(task.getTitle())
                .description(task.getDescription())
                .estimatedDurationMinutes(
                        task.getCorrectedEstimatedMinutes() != null ? task.getCorrectedEstimatedMinutes()
                                : task.getEstimatedHours() > 0 ? (int) (task.getEstimatedHours() * 60) : null)
                .deadline(deadline)
                .scheduledDate(scheduled)
                .priority(task.getPriorityLevel() != null ? task.getPriorityLevel().name() : null)
                .type(task.getType() != null ? task.getType().name() : null)
                .status(convertStatusToTaskService(task))
                .build();
    }

    private String convertStatusToTaskService(PlanningTask task) {
        if (task.getStatus() == null)
            return "TODO";
        return switch (task.getStatus()) {
            case TODO -> "TODO";
            case IN_PROGRESS -> "IN_PROGRESS";
            case COMPLETED -> "COMPLETED";
            case SCHEDULED -> "SCHEDULED";
            case OVERLOADED -> "TODO";
        };
    }
}

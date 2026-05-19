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
 *
 * <p>
 * Converts {@link TaskServiceResponse} (external DTO) to {@link PlanningTask}
 * (domain model) using {@link TaskResponseMapper} to resolve field name and
 * type
 * incompatibilities between the two microservices.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class TaskServiceAdapter implements TaskProviderPort {

    private final TaskServiceClient taskServiceClient;
    private final TaskResponseMapper taskResponseMapper;

    @Override
    public List<PlanningTask> getPendingTasksByUser(String studentId) {
        List<TaskServiceResponse> responses = taskServiceClient.getPendingTasks(studentId);
        return taskResponseMapper.toPlanningTasks(responses);
    }

    @Override
    public List<PlanningTask> getScheduledTasksByUser(String studentId) {
        List<TaskServiceResponse> responses = taskServiceClient.getScheduledTasks(studentId);
        return taskResponseMapper.toPlanningTasks(responses);
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

    // ─────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────

    /**
     * Converts a PlanningTask back to TaskServiceResponse for outbound updates.
     */
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

    /**
     * Converts the internal TaskStatus to the task-service canonical string.
     * SCHEDULED and OVERLOADED are planning-service internals; OVERLOADED maps back
     * to TODO.
     */
    private String convertStatusToTaskService(PlanningTask task) {
        if (task.getStatus() == null)
            return "TODO";
        return switch (task.getStatus()) {
            case TODO -> "TODO";
            case IN_PROGRESS -> "IN_PROGRESS";
            case COMPLETED -> "COMPLETED";
            case SCHEDULED -> "SCHEDULED";
            case OVERLOADED -> "TODO"; // task-service has no OVERLOADED
        };
    }
}

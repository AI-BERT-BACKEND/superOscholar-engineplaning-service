package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.infrastructure.external.feign.client.TaskServiceClient;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import com.aibert.dosw.infrastructure.external.feign.mapper.TaskResponseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Secondary adapter that implements the TaskProviderPort using Feign.
 * Converts TaskServiceResponse (external DTO) to PlanningTask (domain model)
 * using TaskResponseMapper to resolve field name and type incompatibilities.
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
        // Convert PlanningTask back to TaskServiceResponse for the PUT request
        List<TaskServiceResponse> responses = tasks.stream()
                .map(this::toTaskServiceResponse)
                .toList();
        taskServiceClient.updateTaskPriorities(responses);
    }

    @Override
    public void reportTaskFailure(String studentId, String taskId, double hoursMissed, String reason) {
        taskServiceClient.reportTaskFailure(studentId, taskId, hoursMissed, reason);
    }

    /**
     * Convierte PlanningTask de vuelta a TaskServiceResponse para enviar
     * actualizaciones.
     */
    private TaskServiceResponse toTaskServiceResponse(PlanningTask task) {
        return TaskServiceResponse.builder()
                .id(task.getId())
                .studentId(task.getUserId())
                .title(task.getTitle())
                .description(task.getDescription())
                .estimatedDurationMinutes(
                        task.getEstimatedHours() > 0 ? (int) (task.getEstimatedHours() * 60) : null)
                .deadline(task.getDueDate() != null ? task.getDueDate().atStartOfDay() : null)
                .scheduledDate(task.getScheduledDate() != null ? task.getScheduledDate().atStartOfDay() : null)
                .priority(toTaskServicePriority(task))
                .type(task.getType() != null ? task.getType().name() : null)
                .status(convertStatusToTaskService(task))
                .build();
    }

    private String toTaskServicePriority(PlanningTask task) {
        if (task.getPriorityLevel() == null) {
            return null;
        }
        return switch (task.getPriorityLevel()) {
            case CRITICA -> "CRITICAL";
            case ALTA -> "HIGH";
            case MEDIA -> "MEDIUM";
            case BAJA -> "LOW";
        };
    }

    /**
     * Convierte TaskStatus de planning-service a String de task-service.
     * PENDING → TODO, IN_PROGRESS → IN_PROGRESS, COMPLETED → COMPLETED, SCHEDULED →
     * SCHEDULED
     */
    private String convertStatusToTaskService(PlanningTask task) {
        if (task.getStatus() == null) {
            return "TODO";
        }
        return switch (task.getStatus()) {
            case PENDING -> "TODO";
            case IN_PROGRESS -> "IN_PROGRESS";
            case COMPLETED -> "COMPLETED";
            case SCHEDULED -> "SCHEDULED";
            case OVERLOADED -> "TODO"; // task-service doesn't have OVERLOADED
        };
    }
}

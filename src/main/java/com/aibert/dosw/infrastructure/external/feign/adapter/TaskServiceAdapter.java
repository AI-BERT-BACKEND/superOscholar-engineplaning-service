package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.infrastructure.external.feign.client.TaskServiceClient;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponseDTO;
import com.aibert.dosw.infrastructure.external.feign.mapper.TaskResponseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Secondary adapter que implementa TaskProviderPort usando Feign.
 * Actúa como anti-corruption layer: recibe TaskServiceResponseDTO del
 * task-service y los convierte a PlanningTask del dominio de planning-service.
 */
@Component
@RequiredArgsConstructor
public class TaskServiceAdapter implements TaskProviderPort {

    private final TaskServiceClient taskServiceClient;
    private final TaskResponseMapper taskResponseMapper;

    @Override
    public List<PlanningTask> getPendingTasksByUser(String studentId) {
        List<TaskServiceResponseDTO> dtos = taskServiceClient.getPendingTasks(studentId);
        return taskResponseMapper.toDomainList(dtos);
    }

    @Override
    public List<PlanningTask> getScheduledTasksByUser(String studentId) {
        List<TaskServiceResponseDTO> dtos = taskServiceClient.getScheduledTasks(studentId);
        return taskResponseMapper.toDomainList(dtos);
    }

    @Override
    public void updateTaskPriorities(List<PlanningTask> tasks) {
        // Convertir PlanningTasks de vuelta a DTOs para enviar al task-service
        List<TaskServiceResponseDTO> dtos = tasks.stream()
                .map(this::toResponseDTO)
                .toList();
        taskServiceClient.updateTaskPriorities(dtos);
    }

    @Override
    public void reportTaskFailure(String studentId, String taskId, double hoursMissed, String reason) {
        taskServiceClient.reportTaskFailure(studentId, taskId, hoursMissed, reason);
    }

    /**
     * Convierte un PlanningTask de dominio de vuelta a TaskServiceResponseDTO
     * para enviar las prioridades actualizadas al task-service.
     */
    private TaskServiceResponseDTO toResponseDTO(PlanningTask task) {
        return TaskServiceResponseDTO.builder()
                .id(task.getId())
                .studentId(task.getUserId())
                .title(task.getTitle())
                .description(task.getDescription())
                .estimatedDurationMinutes(
                        task.getEstimatedHours() > 0
                                ? (int) Math.round(task.getEstimatedHours() * 60)
                                : null)
                .priority(task.getPriorityLevel() != null
                        ? task.getPriorityLevel().name()
                        : null)
                .status(task.getStatus() != null
                        ? mapStatusToTaskService(task.getStatus())
                        : null)
                .build();
    }

    /**
     * Mapea TaskStatus de planning-service al formato String del task-service.
     * Conversiones inversas: PENDING → TODO, etc.
     */
    private String mapStatusToTaskService(com.aibert.dosw.domain.model.task.TaskStatus status) {
        return switch (status) {
            case PENDING -> "TODO";
            case IN_PROGRESS -> "IN_PROGRESS";
            case COMPLETED -> "COMPLETED";
            case SCHEDULED -> "SCHEDULED";
            case OVERLOADED -> "TODO"; // fallback: task-service no tiene OVERLOADED
        };
    }
}

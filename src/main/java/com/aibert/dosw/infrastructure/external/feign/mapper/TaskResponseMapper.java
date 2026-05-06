package com.aibert.dosw.infrastructure.external.feign.mapper;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte TaskServiceResponse (DTO de task-service) a PlanningTask (dominio).
 * <p>
 * Resuelve las incompatibilidades de tipos entre los dos microservicios:
 * <ul>
 *   <li>studentId → userId</li>
 *   <li>estimatedDurationMinutes (Integer) → estimatedHours (double) : ÷60</li>
 *   <li>deadline (LocalDateTime) → dueDate (LocalDate) : .toLocalDate()</li>
 *   <li>scheduledDate (LocalDateTime) → scheduledDate (LocalDate) : .toLocalDate()</li>
 *   <li>priority (String) → priorityLevel (TaskPriority enum)</li>
 *   <li>status (String: TODO/IN_PROGRESS) → status (TaskStatus enum: PENDING/IN_PROGRESS)</li>
 * </ul>
 */
@Component
public class TaskResponseMapper {

    /**
     * Convierte una lista de respuestas de task-service a PlanningTasks.
     *
     * @param responses lista de DTOs de task-service
     * @return lista de PlanningTask del dominio de planning-service
     */
    public List<PlanningTask> toPlanningTasks(List<TaskServiceResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return Collections.emptyList();
        }
        return responses.stream()
                .map(this::toPlanningTask)
                .toList();
    }

    /**
     * Convierte un TaskServiceResponse individual a PlanningTask.
     */
    public PlanningTask toPlanningTask(TaskServiceResponse response) {
        return PlanningTask.builder()
                .id(response.getId())
                .userId(response.getStudentId())
                .title(response.getTitle())
                .description(response.getDescription())
                .estimatedHours(convertMinutesToHours(response.getEstimatedDurationMinutes()))
                .dueDate(convertToLocalDate(response.getDeadline()))
                .scheduledDate(convertToLocalDate(response.getScheduledDate()))
                .priorityLevel(convertPriority(response.getPriority()))
                .status(convertStatus(response.getStatus()))
                .difficulty(response.getDifficulty() != null ? response.getDifficulty() : 0)
                .subjectName(response.getSubjectId()) // Mapping subjectId as subjectName for now
                .build();
    }

    /**
     * Convierte minutos (Integer) a horas (double).
     * Ej: 90 minutos → 1.5 horas
     */
    private double convertMinutesToHours(Integer minutes) {
        if (minutes == null || minutes <= 0) {
            return 0.0;
        }
        return minutes / 60.0;
    }

    /**
     * Extrae LocalDate de LocalDateTime.
     */
    private LocalDate convertToLocalDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate();
    }

    /**
     * Convierte el String de prioridad al enum TaskPriority.
     */
    private TaskPriority convertPriority(String priority) {
        if (priority == null || priority.isBlank()) {
            return null;
        }
        try {
            return TaskPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convierte el String de estado de task-service al enum TaskStatus de planning-service.
     * Mapeo: TODO → PENDING, IN_PROGRESS → IN_PROGRESS, COMPLETED → COMPLETED,
     * SCHEDULED → SCHEDULED
     */
    private TaskStatus convertStatus(String status) {
        if (status == null || status.isBlank()) {
            return TaskStatus.PENDING;
        }
        return switch (status.toUpperCase()) {
            case "TODO" -> TaskStatus.PENDING;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "SCHEDULED" -> TaskStatus.SCHEDULED;
            default -> TaskStatus.PENDING;
        };
    }
}

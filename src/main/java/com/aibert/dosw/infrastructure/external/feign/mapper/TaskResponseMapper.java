package com.aibert.dosw.infrastructure.external.feign.mapper;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.model.task.TaskType;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper que convierte TaskServiceResponse (DTO de task-service) a PlanningTask
 * (dominio).
 * <p>
 * Resuelve las incompatibilidades de tipos entre los dos microservicios:
 * <ul>
 * <li>studentId → userId</li>
 * <li>estimatedDurationMinutes (Integer) → estimatedHours (double) : ÷60</li>
 * <li>deadline (LocalDateTime) → dueDate (LocalDate) : .toLocalDate()</li>
 * <li>scheduledDate (LocalDateTime) → scheduledDate (LocalDate) :
 * .toLocalDate()</li>
 * <li>priority (String) → priorityLevel (TaskPriority enum)</li>
 * <li>status (String: TODO/IN_PROGRESS) → status (TaskStatus enum:
 * PENDING/IN_PROGRESS)</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface TaskResponseMapper {

    /**
     * Convierte una lista de respuestas de task-service a PlanningTasks.
     *
     * @param responses lista de DTOs de task-service
     * @return lista de PlanningTask del dominio de planning-service
     */
    default List<PlanningTask> toPlanningTasks(List<TaskServiceResponse> responses) {
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
    @Mapping(target = "userId", source = "studentId")
    @Mapping(target = "estimatedHours", source = "estimatedDurationMinutes", qualifiedByName = "minutesToHours")
    @Mapping(target = "dueDate", source = "deadline", qualifiedByName = "toLocalDate")
    @Mapping(target = "scheduledDate", source = "scheduledDate", qualifiedByName = "toLocalDate")
    @Mapping(target = "priorityLevel", source = "priority", qualifiedByName = "priorityFromString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusFromString")
    @Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "difficultyOrDefault")
    @Mapping(target = "subjectName", source = "subjectId")
    @Mapping(target = "type", source = "type", qualifiedByName = "typeFromString")
    PlanningTask toPlanningTask(TaskServiceResponse response);

    /**
     * Convierte minutos (Integer) a horas (double).
     * Ej: 90 minutos → 1.5 horas
     */
    @Named("minutesToHours")
    default double convertMinutesToHours(Integer minutes) {
        if (minutes == null || minutes <= 0) {
            return 0.0;
        }
        return minutes / 60.0;
    }

    /**
     * Extrae LocalDate de LocalDateTime.
     */
    @Named("toLocalDate")
    default LocalDate convertToLocalDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate();
    }

    /**
     * Convierte el String de prioridad al enum TaskPriority.
     */
    @Named("priorityFromString")
    default TaskPriority convertPriority(String priority) {
        if (priority == null || priority.isBlank()) {
            return null;
        }
        return switch (priority.toUpperCase()) {
            // Nuevos valores del planning-service
            case "ALTA" -> TaskPriority.ALTA;
            case "MEDIA" -> TaskPriority.MEDIA;
            case "BAJA" -> TaskPriority.BAJA;
            case "CRITICA" -> TaskPriority.CRITICA;
            // Compatibilidad con valores legacy del task-service
            case "HIGH", "CRITICAL" -> TaskPriority.ALTA;
            case "MEDIUM" -> TaskPriority.MEDIA;
            case "LOW" -> TaskPriority.BAJA;
            default -> {
                try {
                    yield TaskPriority.valueOf(priority.toUpperCase());
                } catch (IllegalArgumentException e) {
                    yield null;
                }
            }
        };
    }

    /**
     * Convierte el String de estado de task-service al enum TaskStatus de
     * planning-service.
     * Mapeo: TODO → PENDING, IN_PROGRESS → IN_PROGRESS, COMPLETED → COMPLETED,
     * SCHEDULED → SCHEDULED
     */
    @Named("statusFromString")
    default TaskStatus convertStatus(String status) {
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

    @Named("difficultyOrDefault")
    default int convertDifficulty(Integer difficulty) {
        return difficulty != null ? difficulty : 0;
    }

    @Named("typeFromString")
    default TaskType convertType(String type) {
        if (type == null || type.isBlank()) {
            return TaskType.OTRO;
        }
        return switch (type.toUpperCase()) {
            case "TAREA" -> TaskType.TAREA;
            case "EXAMEN" -> TaskType.EXAMEN;
            case "PROYECTO" -> TaskType.PROYECTO;
            case "LECTURA" -> TaskType.LECTURA;
            default -> TaskType.OTRO;
        };
    }
}

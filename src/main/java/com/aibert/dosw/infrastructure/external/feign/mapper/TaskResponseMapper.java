package com.aibert.dosw.infrastructure.external.feign.mapper;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponseDTO;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte TaskServiceResponseDTO (contrato de task-service)
 * a PlanningTask (modelo de dominio de planning-service).
 *
 * Conversiones que realiza:
 * - studentId → userId
 * - estimatedDurationMinutes (Integer) → estimatedHours (double) ÷60
 * - deadline (LocalDateTime) → dueDate (LocalDate)
 * - scheduledDate (LocalDateTime) → scheduledDate (LocalDate)
 * - priority (String) → priorityLevel (TaskPriority enum)
 * - status (String "TODO"/"IN_PROGRESS") → TaskStatus (PENDING/IN_PROGRESS)
 * - Campos faltantes en task-service reciben defaults seguros
 */
@Component
public class TaskResponseMapper {

    /**
     * Convierte una lista de DTOs del task-service a PlanningTasks del dominio.
     *
     * @param dtos lista de respuestas del task-service (puede ser null)
     * @return lista de PlanningTask del dominio (nunca null)
     */
    public List<PlanningTask> toDomainList(List<TaskServiceResponseDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Convierte un único DTO del task-service a PlanningTask del dominio.
     *
     * @param dto respuesta del task-service
     * @return PlanningTask del dominio
     */
    public PlanningTask toDomain(TaskServiceResponseDTO dto) {
        return PlanningTask.builder()
                .id(dto.getId())
                .userId(dto.getStudentId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .estimatedHours(convertMinutesToHours(dto.getEstimatedDurationMinutes()))
                .dueDate(dto.getDeadline() != null ? dto.getDeadline().toLocalDate() : null)
                .scheduledDate(dto.getScheduledDate() != null ? dto.getScheduledDate().toLocalDate() : null)
                .priorityLevel(mapPriority(dto.getPriority()))
                .status(mapStatus(dto.getStatus()))
                .difficulty(dto.getDifficulty() != null ? dto.getDifficulty() : 3)
                .subjectName(dto.getSubjectId() != null ? dto.getSubjectId() : "Sin asignatura")
                .subjectCredits(dto.getSubjectCredits() != null ? dto.getSubjectCredits() : 3)
                .taskWeightInGrade(dto.getTaskWeightInGrade())
                .evaluationCuts(Collections.emptyList())
                .build();
    }

    /**
     * Convierte minutos (Integer del task-service) a horas (double del planning-service).
     * Si es null, retorna 1.0h como estimación mínima por defecto.
     */
    double convertMinutesToHours(Integer minutes) {
        if (minutes == null || minutes <= 0) {
            return 1.0; // default: al menos 1 hora
        }
        return minutes / 60.0;
    }

    /**
     * Mapea el String de prioridad del task-service al enum TaskPriority.
     * Soporta variaciones comunes: "HIGH", "ALTA", "high", etc.
     */
    TaskPriority mapPriority(String priority) {
        if (priority == null || priority.isBlank()) {
            return TaskPriority.MEDIUM;
        }
        try {
            return TaskPriority.valueOf(priority.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Fallback para nombres en español u otros formatos
            return switch (priority.toUpperCase().trim()) {
                case "ALTA", "URGENTE" -> TaskPriority.HIGH;
                case "BAJA" -> TaskPriority.LOW;
                case "MEDIA" -> TaskPriority.MEDIUM;
                case "CRITICA", "CRÍTICA" -> TaskPriority.CRITICAL;
                default -> TaskPriority.MEDIUM;
            };
        }
    }

    /**
     * Mapea el String de estado del task-service al enum TaskStatus de planning-service.
     * Conversiones clave: "TODO" → PENDING, "IN_PROGRESS" → IN_PROGRESS.
     */
    TaskStatus mapStatus(String status) {
        if (status == null || status.isBlank()) {
            return TaskStatus.PENDING;
        }
        return switch (status.toUpperCase().trim()) {
            case "TODO", "PENDIENTE", "PENDING" -> TaskStatus.PENDING;
            case "IN_PROGRESS", "EN_PROGRESO" -> TaskStatus.IN_PROGRESS;
            case "COMPLETED", "COMPLETADO", "DONE" -> TaskStatus.COMPLETED;
            case "SCHEDULED", "PROGRAMADO" -> TaskStatus.SCHEDULED;
            case "OVERLOADED", "SOBRECARGADO" -> TaskStatus.OVERLOADED;
            default -> TaskStatus.PENDING;
        };
    }
}

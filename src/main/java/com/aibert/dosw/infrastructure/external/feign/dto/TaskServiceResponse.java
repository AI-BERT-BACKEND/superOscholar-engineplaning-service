package com.aibert.dosw.infrastructure.external.feign.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la respuesta real de task-service (TaskResponse).
 * Los nombres y tipos de campo coinciden con lo que task-service expone:
 * - studentId (no userId)
 * - estimatedDurationMinutes (Integer, no double hours)
 * - deadline (LocalDateTime, no LocalDate)
 * - scheduledDate (LocalDateTime, no LocalDate)
 * - priority (String, no TaskPriority enum)
 * - status (String con valores TODO/IN_PROGRESS/COMPLETED/SCHEDULED)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskServiceResponse {

    private String id;
    private String studentId;
    private String title;
    private String description;

    /** Duración estimada en minutos (task-service usa minutos, planning usa horas) */
    private Integer estimatedDurationMinutes;

    /** Fecha límite como LocalDateTime (planning-service necesita solo LocalDate) */
    private LocalDateTime deadline;

    /** Fecha programada como LocalDateTime (planning-service necesita solo LocalDate) */
    private LocalDateTime scheduledDate;

    /** Nombre de la prioridad (LOW, MEDIUM, HIGH, CRITICAL) */
    private String priority;

    /** Estado de la tarea (TODO, IN_PROGRESS, COMPLETED, SCHEDULED) */
    private String status;

    /** ID de la materia asociada */
    private String subjectId;

    /** Dificultad de la tarea (1-5), puede ser null si task-service no lo tiene */
    private Integer difficulty;
}

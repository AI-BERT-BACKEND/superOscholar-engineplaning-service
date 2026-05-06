package com.aibert.dosw.infrastructure.external.feign.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO que representa la respuesta real del task-service.
 * Se usa como anti-corruption layer para desacoplar el modelo de dominio
 * de planning-service del contrato del task-service.
 *
 * Campos esperados del task-service (powerpuff-engineers-task-service):
 * - id, studentId, title, description
 * - estimatedDurationMinutes (Integer) → se convierte a estimatedHours (double)
 * - deadline (LocalDateTime) → se convierte a dueDate (LocalDate)
 * - scheduledDate (LocalDateTime) → se convierte a scheduledDate (LocalDate)
 * - priority (String) → se convierte a TaskPriority enum
 * - status (String) → se convierte a TaskStatus enum
 * - subjectId (String) → se usa como subjectName (nombre parcial)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class TaskServiceResponseDTO {

    private String id;
    private String studentId;
    private String title;
    private String description;

    // task-service usa minutos como Integer; planning-service necesita horas como double
    private Integer estimatedDurationMinutes;

    // task-service usa LocalDateTime; planning-service necesita LocalDate
    private LocalDateTime deadline;
    private LocalDateTime scheduledDate;

    // task-service usa String para el enum (ej: "TODO", "IN_PROGRESS", "COMPLETED")
    private String status;

    // task-service usa String para la prioridad (ej: "HIGH", "MEDIUM", "LOW")
    private String priority;

    // task-service solo tiene subjectId (no subjectName, credits, weight, etc.)
    private String subjectId;

    // Campos opcionales que podrían existir en futuras versiones del task-service
    private Integer difficulty;
    private Integer subjectCredits;
    private Double taskWeightInGrade;
}

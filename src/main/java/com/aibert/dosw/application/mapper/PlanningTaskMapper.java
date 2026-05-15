package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.dto.response.MovedTaskResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.dto.response.ScheduledBlockResponse;
import com.aibert.dosw.domain.model.context.MovedTaskRecord;
import com.aibert.dosw.domain.model.schedule.ScheduledBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting Domain entities to Application DTOs.
 *
 * <p>
 * Handles the mapping between internal domain models and the external
 * task-service JSON schema expected by clients (AIB-22 data contract).
 * The {@code priorityLevel} is serialized using the canonical English enum
 * name (CRITICAL / HIGH / MEDIUM / LOW) — no translation needed since domain
 * enum values already match the contract.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PlanningTaskMapper {

    /**
     * Converts a {@link PlanningTask} domain model into a {@link PrioritizedTaskResponse} DTO.
     *
     * @param task The domain task
     * @return The response DTO aligned with AIB-22 output contract
     */
    @Mapping(target = "taskId",    source = "id")
    @Mapping(target = "subjectId", source = "subjectName")
    @Mapping(target = "taskType",  expression = "java(task.getType() != null ? task.getType().name() : \"OTRO\")")
    @Mapping(target = "estimatedDurationMinutes", expression = "java((int) Math.round(task.getEstimatedHours() * 60))")
    @Mapping(target = "deadline",      expression = "java(task.getDueDate() != null ? task.getDueDate().atTime(23, 59) : null)")
    @Mapping(target = "scheduledDate", expression = "java(task.getScheduledDate() != null ? task.getScheduledDate().atStartOfDay() : null)")
    @Mapping(target = "status",        expression = "java(mapStatus(task))")
    @Mapping(target = "priorityLevel", expression = "java(mapPriority(task))")
    @Mapping(target = "lastUpdated",   expression = "java(java.time.LocalDateTime.now())")
    PrioritizedTaskResponse toPrioritizedResponse(PlanningTask task);

    /**
     * Maps the domain TaskPriority enum to the canonical API string.
     * Enum values already use English names — no translation required.
     */
    default String mapPriority(PlanningTask task) {
        if (task == null || task.getPriorityLevel() == null) {
            return "LOW";
        }
        return task.getPriorityLevel().name(); // CRITICAL, HIGH, MEDIUM, LOW
    }

    /**
     * Maps the internal TaskStatus enum to the task-service canonical string.
     * Internal states (SCHEDULED, OVERLOADED) are projected back to TODO.
     */
    default String mapStatus(PlanningTask task) {
        if (task == null || task.getStatus() == null) {
            return "TODO";
        }
        return switch (task.getStatus()) {
            case TODO       -> "TODO";
            case IN_PROGRESS -> "IN_PROGRESS";
            case COMPLETED  -> "COMPLETED";
            case SCHEDULED  -> "SCHEDULED";
            case OVERLOADED -> "TODO";
        };
    }

    @Mapping(target = "durationHours", expression = "java(block.getDurationHours())")
    ScheduledBlockResponse toScheduledBlockResponse(ScheduledBlock block);

    /**
     * Converts a MovedTaskRecord domain model to a MovedTaskResponse DTO.
     */
    @Mapping(target = "taskId",           source = "taskId")
    @Mapping(target = "taskTitle",        source = "taskTitle")
    @Mapping(target = "originalDate",     source = "originalDate")
    @Mapping(target = "originalStartTime", source = "originalStartTime")
    @Mapping(target = "newDate",          source = "newDate")
    @Mapping(target = "newStartTime",     source = "newStartTime")
    @Mapping(target = "reason",           source = "reason")
    MovedTaskResponse toMovedTaskResponse(MovedTaskRecord record);

    @Mapping(target = "fullyAssigned",  expression = "java(plan.isFullyAssigned())")
    @Mapping(target = "criticalAlerts", expression = "java(plan.getCriticalTasks().stream().map(this::toPrioritizedResponse).toList())")
    @Mapping(target = "movedTasks",     expression = "java(plan.getMovedTasks() != null ? plan.getMovedTasks().stream().map(this::toMovedTaskResponse).toList() : java.util.List.of())")
    @Mapping(target = "message",        expression = "java(plan.isFullyAssigned() ? \"¡Plan de trabajo generado exitosamente!\" : \"Plan generado. Hay tareas que no pudieron ser asignadas por falta de disponibilidad.\")")
    DistributionPlanResponse toDistributionPlanResponse(WeeklyDistributionPlan plan);
}

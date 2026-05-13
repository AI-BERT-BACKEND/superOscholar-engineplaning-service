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
 * Handles the mapping between internal domain names and the external
 * task-service schema.
 */
@Mapper(componentModel = "spring")
public interface PlanningTaskMapper {

    /**
     * Converts a PlanningTask domain model into a PrioritizedTaskResponse DTO.
     * The priorityLevel is projected using the label (ALTA/MEDIA/BAJA) per R14 spec.
     *
     * @param task The domain task
     * @return The response DTO
     */
    @Mapping(target = "taskId", source = "id")
    // External task-service schema expects subjectId, which is sourced from subjectName here.
    @Mapping(target = "subjectId", source = "subjectName")
    @Mapping(target = "estimatedDurationMinutes", expression = "java((int) Math.round(task.getEstimatedHours() * 60))")
    @Mapping(target = "deadline", expression = "java(task.getDueDate() != null ? task.getDueDate().atTime(23, 59) : null)")
    // Use label (ALTA/MEDIA/BAJA) instead of enum name to comply with R14 spec
    @Mapping(target = "priorityLevel", expression = "java(task.getPriorityLevel() != null ? task.getPriorityLevel().getLabel() : null)")
    PrioritizedTaskResponse toPrioritizedResponse(PlanningTask task);

    @Mapping(target = "durationHours", expression = "java(block.getDurationHours())")
    ScheduledBlockResponse toScheduledBlockResponse(ScheduledBlock block);

    /**
     * Converts a MovedTaskRecord domain model to a MovedTaskResponse DTO.
     */
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "taskTitle", source = "taskTitle")
    @Mapping(target = "originalDate", source = "originalDate")
    @Mapping(target = "originalStartTime", source = "originalStartTime")
    @Mapping(target = "newDate", source = "newDate")
    @Mapping(target = "newStartTime", source = "newStartTime")
    @Mapping(target = "reason", source = "reason")
    MovedTaskResponse toMovedTaskResponse(MovedTaskRecord record);

    @Mapping(target = "fullyAssigned", expression = "java(plan.isFullyAssigned())")
    @Mapping(target = "criticalAlerts", expression = "java(plan.getCriticalTasks().stream().map(this::toPrioritizedResponse).toList())")
    @Mapping(target = "movedTasks", expression = "java(plan.getMovedTasks() != null ? plan.getMovedTasks().stream().map(this::toMovedTaskResponse).toList() : java.util.List.of())")
    @Mapping(target = "message", expression = "java(plan.isFullyAssigned() ? \"¡Plan de trabajo generado exitosamente!\" : \"Plan generado. Hay tareas que no pudieron ser asignadas por falta de disponibilidad.\")")
    DistributionPlanResponse toDistributionPlanResponse(WeeklyDistributionPlan plan);
}


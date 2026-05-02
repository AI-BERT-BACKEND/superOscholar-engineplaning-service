package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.dto.response.ScheduledBlockResponse;
import com.aibert.dosw.domain.model.schedule.ScheduledBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting Domain entities to Application DTOs.
 * Handles the mapping between internal domain names and the external task-service schema.
 */
@Mapper(componentModel = "spring")
public interface PlanningTaskMapper {

    /**
     * Converts a PlanningTask domain model into a PrioritizedTaskResponse DTO.
     * 
     * @param task The domain task
     * @return The response DTO
     */
    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "subjectId", source = "subjectName")
    @Mapping(target = "estimatedDurationMinutes", expression = "java((int) Math.round(task.getEstimatedHours() * 60))")
    @Mapping(target = "deadline", expression = "java(task.getDueDate() != null ? task.getDueDate().atTime(23, 59) : null)")
    PrioritizedTaskResponse toPrioritizedResponse(PlanningTask task);

    @Mapping(target = "durationHours", expression = "java(block.getDurationHours())")
    ScheduledBlockResponse toScheduledBlockResponse(ScheduledBlock block);

    @Mapping(target = "fullyAssigned", expression = "java(plan.isFullyAssigned())")
    @Mapping(target = "criticalAlerts", expression = "java(plan.getCriticalTasks().stream().map(this::toPrioritizedResponse).collect(java.util.stream.Collectors.toList()))")
    DistributionPlanResponse toDistributionPlanResponse(WeeklyDistributionPlan plan);
}

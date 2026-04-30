package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.PlanningTaskEntity;
import org.springframework.stereotype.Component;

@Component
public class PlanningTaskEntityMapper {

    public PlanningTask toDomain(PlanningTaskEntity entity) {
        if (entity == null) {
            return null;
        }

        return PlanningTask.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .estimatedHours(entity.getEstimatedHours())
                .difficulty(entity.getDifficulty())
                .dueDate(entity.getDueDate())
                .scheduledDate(entity.getScheduledDate())
                .subjectName(entity.getSubjectName())
                .subjectCredits(entity.getSubjectCredits())
                .taskWeightInGrade(entity.getTaskWeightInGrade())
                .gradePeriod1(entity.getGradePeriod1())
                .gradePeriod2(entity.getGradePeriod2())
                .weightPeriod1(entity.getWeightPeriod1())
                .weightPeriod2(entity.getWeightPeriod2())
                .weightPeriod3(entity.getWeightPeriod3())
                .priorityScore(entity.getPriorityScore())
                .priorityLevel(parsePriority(entity.getPriorityLevel()))
                .status(parseStatus(entity.getStatus()))
                .build();
    }

    public PlanningTaskEntity toEntity(PlanningTask task) {
        if (task == null) {
            return null;
        }

        TaskStatus status = task.getStatus() != null
                ? task.getStatus()
                : TaskStatus.PENDING;

        return PlanningTaskEntity.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .description(task.getDescription())
                .estimatedHours(task.getEstimatedHours())
                .difficulty(task.getDifficulty())
                .dueDate(task.getDueDate())
                .scheduledDate(task.getScheduledDate())
                .subjectName(task.getSubjectName())
                .subjectCredits(task.getSubjectCredits())
                .taskWeightInGrade(task.getTaskWeightInGrade())
                .gradePeriod1(task.getGradePeriod1())
                .gradePeriod2(task.getGradePeriod2())
                .weightPeriod1(task.getWeightPeriod1())
                .weightPeriod2(task.getWeightPeriod2())
                .weightPeriod3(task.getWeightPeriod3())
                .priorityScore(task.getPriorityScore())
                .priorityLevel(formatPriority(task.getPriorityLevel()))
                .status(status.name())
                .build();
    }

    private TaskPriority parsePriority(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return TaskPriority.valueOf(value);
    }

    private String formatPriority(TaskPriority value) {
        return value != null ? value.name() : null;
    }

    private TaskStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return TaskStatus.valueOf(value);
    }
}

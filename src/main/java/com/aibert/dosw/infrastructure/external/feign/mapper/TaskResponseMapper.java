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
 * Mapper that converts {@link TaskServiceResponse} (task-service DTO) to
 * {@link PlanningTask} (domain model).
 *
 * <p>
 * Resolves type and naming incompatibilities between the two microservices:
 * </p>
 * <ul>
 * <li>{@code studentId} → {@code userId}</li>
 * <li>{@code estimatedDurationMinutes} (Integer) → {@code estimatedHours}
 * (double): ÷ 60</li>
 * <li>{@code deadline} (LocalDateTime) → {@code dueDateTime}
 * (LocalDateTime)</li>
 * <li>{@code deadline} (LocalDateTime) → {@code dueDate} (LocalDate):
 * toLocalDate()</li>
 * <li>{@code scheduledDate} (LocalDateTime) → {@code scheduledDateTime}
 * (LocalDateTime)</li>
 * <li>{@code scheduledDate} (LocalDateTime) → {@code scheduledDate}
 * (LocalDate)</li>
 * <li>{@code priority} (String: LOW/MEDIUM/HIGH/CRITICAL) →
 * {@code priorityLevel} (TaskPriority)</li>
 * <li>{@code status} (String: TODO/IN_PROGRESS/COMPLETED) → {@code status}
 * (TaskStatus)</li>
 * <li>{@code subjectId} → {@code subjectName} (planning-service internal
 * field)</li>
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface TaskResponseMapper {

    /**
     * Converts a list of task-service responses to PlanningTask domain objects.
     *
     * @param responses List of task-service DTOs
     * @return List of PlanningTask domain objects
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
     * Converts a single {@link TaskServiceResponse} to a {@link PlanningTask}.
     */
    @Mapping(target = "userId", source = "studentId")
    @Mapping(target = "estimatedHours", source = "estimatedDurationMinutes", qualifiedByName = "minutesToHours")
    @Mapping(target = "dueDate", source = "deadline", qualifiedByName = "toLocalDate")
    @Mapping(target = "dueDateTime", source = "deadline")
    @Mapping(target = "scheduledDate", source = "scheduledDate", qualifiedByName = "toLocalDate")
    @Mapping(target = "scheduledDateTime", source = "scheduledDate")
    @Mapping(target = "priorityLevel", source = "priority", qualifiedByName = "priorityFromString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusFromString")
    @Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "difficultyOrDefault")
    @Mapping(target = "subjectId", source = "subjectId")
    @Mapping(target = "subjectName", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "typeFromString")
    @Mapping(target = "subjectCredits", ignore = true)
    @Mapping(target = "taskWeightInGrade", ignore = true)
    @Mapping(target = "evaluationCuts", ignore = true)
    @Mapping(target = "priorityScore", ignore = true)
    @Mapping(target = "lastPrioritizedAt", ignore = true)
    PlanningTask toPlanningTask(TaskServiceResponse response);

    // ─────────────────────────────────────────────────────────────────
    // Named conversion helpers
    // ─────────────────────────────────────────────────────────────────

    /** Converts minutes (Integer) to hours (double). E.g. 90 min → 1.5 h. */
    @Named("minutesToHours")
    default double convertMinutesToHours(Integer minutes) {
        if (minutes == null || minutes <= 0)
            return 0.0;
        return minutes / 60.0;
    }

    /** Extracts a {@link LocalDate} from a {@link LocalDateTime}. */
    @Named("toLocalDate")
    default LocalDate convertToLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }

    /**
     * Converts the task-service priority string to the domain {@link TaskPriority}
     * enum.
     * Accepted values (case-insensitive): CRITICAL, HIGH, MEDIUM, LOW.
     */
    @Named("priorityFromString")
    default TaskPriority convertPriority(String priority) {
        if (priority == null || priority.isBlank())
            return null;
        return switch (priority.toUpperCase()) {
            case "CRITICAL" -> TaskPriority.CRITICAL;
            case "HIGH" -> TaskPriority.HIGH;
            case "MEDIUM" -> TaskPriority.MEDIUM;
            case "LOW" -> TaskPriority.LOW;
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
     * Converts the task-service status string to the domain {@link TaskStatus}
     * enum.
     * Mapping: TODO → TODO, IN_PROGRESS → IN_PROGRESS, COMPLETED → COMPLETED,
     * SCHEDULED → SCHEDULED. Unknown values default to TODO.
     */
    @Named("statusFromString")
    default TaskStatus convertStatus(String status) {
        if (status == null || status.isBlank())
            return TaskStatus.TODO;
        return switch (status.toUpperCase()) {
            case "TODO" -> TaskStatus.TODO;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "SCHEDULED" -> TaskStatus.SCHEDULED;
            default -> TaskStatus.TODO;
        };
    }

    /** Returns the difficulty value, defaulting to 0 if null. */
    @Named("difficultyOrDefault")
    default int convertDifficulty(Integer difficulty) {
        return difficulty != null ? difficulty : 0;
    }

    /** Converts the task type string to the {@link TaskType} enum. */
    @Named("typeFromString")
    default TaskType convertType(String type) {
        if (type == null || type.isBlank())
            return TaskType.OTRO;
        return switch (type.toUpperCase()) {
            case "TAREA" -> TaskType.TAREA;
            case "EXAMEN" -> TaskType.EXAMEN;
            case "PROYECTO" -> TaskType.PROYECTO;
            case "LECTURA" -> TaskType.LECTURA;
            default -> TaskType.OTRO;
        };
    }
}

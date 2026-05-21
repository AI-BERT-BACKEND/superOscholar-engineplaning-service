package com.aibert.dosw.application.event;

import com.aibert.dosw.domain.model.task.TaskChangeEventType;
import java.util.Objects;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Internal Spring application event fired when a task change is detected.
 * Carries the studentId and task metadata needed to trigger automatic
 * prioritization (AIB-22.1) in the background.
 *
 * <p>All fields are validated in the constructor to ensure only well-formed,
 * non-null values are stored and later processed by the listener.
 */
@Getter
public class TaskChangeEvent extends ApplicationEvent {

    private final String studentId;
    private final String taskId;
    private final TaskChangeEventType eventType;

    public TaskChangeEvent(Object source, String studentId, String taskId, TaskChangeEventType eventType) {
        super(source);
        this.studentId = Objects.requireNonNull(studentId, "studentId must not be null");
        this.taskId = Objects.requireNonNull(taskId, "taskId must not be null");
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
    }
}

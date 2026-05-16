package com.aibert.dosw.application.event;

import com.aibert.dosw.domain.model.task.TaskChangeEventType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Internal Spring application event fired when a task change is detected.
 * Carries the studentId and task metadata needed to trigger automatic
 * prioritization (AIB-22.1) in the background.
 */
@Getter
public class TaskChangeEvent extends ApplicationEvent {

    private final String studentId;
    private final String taskId;
    private final TaskChangeEventType eventType;

    public TaskChangeEvent(Object source, String studentId, String taskId, TaskChangeEventType eventType) {
        super(source);
        this.studentId = studentId;
        this.taskId = taskId;
        this.eventType = eventType;
    }
}

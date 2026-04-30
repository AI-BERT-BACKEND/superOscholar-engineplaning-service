package com.aibert.dosw.domain.model.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Current status of a task in the planning system.
 */
@Getter
@RequiredArgsConstructor
public enum TaskStatus {

    PENDING("Pending", "Task created but not started"),
    SCHEDULED("Scheduled", "Assigned to a specific day"),
    IN_PROGRESS("In progress", "Student is working on it"),
    OVERLOADED("Overloaded", "Does not fit in any available day"),
    COMPLETED("Completed", "Task finished and submitted");

    private final String displayName;
    private final String description;
}

package com.aibert.dosw.domain.model.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Lifecycle status of a task in the planning system.
 *
 * <p>
 * The canonical contract values exposed by task-service are:
 * {@code TODO}, {@code IN_PROGRESS}, {@code COMPLETED}.
 * Planning-service adds internal states {@code SCHEDULED} and {@code OVERLOADED}
 * which are translated back to task-service values at the boundary.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum TaskStatus {

    /** Task created but not yet started (maps to task-service "TODO"). */
    TODO("Todo", "Task created but not started"),
    /** Task assigned to a specific study block (planning-service internal). */
    SCHEDULED("Scheduled", "Assigned to a specific day"),
    /** Student is currently working on it. */
    IN_PROGRESS("In progress", "Student is working on it"),
    /** Task does not fit in any available day (planning-service internal). */
    OVERLOADED("Overloaded", "Does not fit in any available day"),
    /** Task finished and submitted. */
    COMPLETED("Completed", "Task finished and submitted");

    private final String displayName;
    private final String description;
}

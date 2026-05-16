package com.aibert.dosw.domain.model.task;

/**
 * Types of task lifecycle events that trigger automatic prioritization
 * (AIB-22.1).
 * Fired whenever a task is created, updated, or completed in the task-service.
 */
public enum TaskChangeEventType {

    /** A new task has been registered by the student. */
    NUEVA_TAREA,

    /**
     * An existing task has been edited (e.g., deadline, type, or estimated hours
     * changed).
     */
    EDICION,

    /** A task has been marked as completed by the student. */
    COMPLETADO
}

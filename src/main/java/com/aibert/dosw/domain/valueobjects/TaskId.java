package com.aibert.dosw.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

/**
 * Value object that represents the unique task identifier.
 * Immutable and validated on creation.
 */
@Getter
public final class TaskId {

    private final String value;

    private TaskId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "TaskId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * Creates a TaskId from an existing string.
     */
    public static TaskId of(String value) {
        return new TaskId(value);
    }

    /**
     * Generates a new TaskId using a random UUID.
     */
    public static TaskId generate() {
        return new TaskId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaskId other))
            return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

package com.aibert.dosw.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskIdTest {

    @Test
    void shouldCreateAndCompare() {
        TaskId id1 = TaskId.of("task-1");
        TaskId id2 = TaskId.of("task-1");
        TaskId id3 = TaskId.of("task-2");

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals("task-1", id1.toString());
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldGenerate() {
        TaskId generated = TaskId.generate();
        assertNotNull(generated.getValue());
        assertFalse(generated.getValue().isBlank());
    }

    @Test
    void shouldRejectBlankValues() {
        assertThrows(IllegalArgumentException.class, () -> TaskId.of(" "));
    }
}

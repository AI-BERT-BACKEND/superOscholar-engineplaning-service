package com.aibert.dosw.domain.valueobjects;

import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PriorityScoreTest {

    @Test
    void shouldHandleNullDueDate() {
        // FA-02: No deadline → LOW priority with score 0.0
        PriorityScore score = PriorityScore.calculate(null, 0.5, 2.0);

        assertNotNull(score);
        assertEquals(0.0, score.getFinalScore());
        assertEquals(TaskPriority.LOW, score.getLevel());
    }

    @Test
    void shouldReturnCriticalForImminentDeadline() {
        // RN-02: Deadline < 24 h → CRITICAL with score 100.0
        PriorityScore score = PriorityScore.calculate(LocalDate.now(), 0.5, 20.0);

        assertEquals(TaskPriority.CRITICAL, score.getLevel());
        assertEquals(100.0, score.getFinalScore());
    }

    @Test
    void shouldReturnHighForHighScore() {
        // Score ≥ 70 → HIGH
        PriorityScore score = PriorityScore.calculate(
                LocalDate.now().plusDays(2), 1.0, 5.0, 0.40, 0.40, 0.20);

        assertNotNull(score);
        // With 1.0 academic weight + proximity 80 (within 48h) → HIGH expected
        assertEquals(TaskPriority.HIGH, score.getLevel());
    }

    @Test
    void shouldReturnLowForDistantDeadlineAndLowWeight() {
        // Low weight + far deadline → LOW
        PriorityScore score = PriorityScore.calculate(
                LocalDate.now().plusDays(30), 0.0, 0.5, 0.40, 0.40, 0.20);

        assertNotNull(score);
        assertEquals(TaskPriority.LOW, score.getLevel());
    }
}

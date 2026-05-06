package com.aibert.dosw.domain.valueobjects;

import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PriorityScoreTest {

    @Test
    void shouldHandleNullDueDate() {
        PriorityScore score = PriorityScore.calculate(null, 50.0, 2.0);

        assertNotNull(score);
        assertEquals(24.0, score.getFinalScore());
        assertEquals(TaskPriority.LOW, score.getLevel());
    }

    @Test
    void shouldReturnCriticalForImminentDeadline() {
        PriorityScore score = PriorityScore.calculate(LocalDate.now().plusDays(1), 100.0, 20.0);

        assertEquals(TaskPriority.CRITICAL, score.getLevel());
    }
}

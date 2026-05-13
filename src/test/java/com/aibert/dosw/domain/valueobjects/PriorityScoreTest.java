package com.aibert.dosw.domain.valueobjects;

import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PriorityScoreTest {

    @Test
    void shouldHandleNullDueDate() {
        PriorityScore score = PriorityScore.calculate(null, 2.5, 2.0);

        assertNotNull(score);
        assertEquals(24.0, score.getFinalScore());
        assertEquals(TaskPriority.BAJA, score.getLevel());
    }

    @Test
    void shouldReturnCriticalForImminentDeadline() {
        PriorityScore score = PriorityScore.calculate(LocalDate.now(), 5.0, 20.0);

        assertEquals(TaskPriority.CRITICA, score.getLevel());
    }
}

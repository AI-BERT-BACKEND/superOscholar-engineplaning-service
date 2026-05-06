package com.aibert.dosw.domain.model.balance;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BalanceSuggestionTest {

    @Test
    void shouldBuildSuggestionMessage() {
        PlanningTask task = PlanningTask.builder()
                .id("t1")
                .title("Read chapter")
                .build();

        BalanceSuggestion suggestion = BalanceSuggestion.builder()
                .taskToMove(task)
                .fromDate(LocalDate.of(2026, 5, 5))
                .toDate(LocalDate.of(2026, 5, 6))
                .reason("Free time")
                .build();

        assertTrue(suggestion.getSuggestionMessage().contains("Read chapter"));
    }
}

package com.aibert.dosw.domain.model.balance;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain model representing a suggestion to move a task 
 * to balance the weekly workload.
 */
@Getter
@Builder
public class BalanceSuggestion {
    private final PlanningTask taskToMove;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String reason;

    /**
     * Human readable description of the suggestion.
     */
    public String getSuggestionMessage() {
        return String.format("Move task '%s' from %s to %s to balance workload.", 
                taskToMove.getTitle(), fromDate.toString(), toDate.toString());
    }
}

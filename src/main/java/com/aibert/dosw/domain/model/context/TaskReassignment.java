package com.aibert.dosw.domain.model.context;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents the reassignment of a task from one day to another.
 * Produced during the dynamic rebalancing process (R17).
 */
@Getter
@Builder
public class TaskReassignment {

    private final PlanningTask task;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String reason;

    /**
     * Factory method for creating a reassignment.
     *
     * @param task     task being reassigned
     * @param fromDate original overloaded day
     * @param toDate   new day with available time
     * @param reason   reason for the reassignment
     */
    public static TaskReassignment of(
            PlanningTask task,
            LocalDate fromDate,
            LocalDate toDate,
            String reason) {

        return TaskReassignment.builder()
                .task(task)
                .fromDate(fromDate)
                .toDate(toDate)
                .reason(reason)
                .build();
    }

    /**
     * Human-readable description of the reassignment.
     */
    public String getSummary() {
        return String.format(
                "Task '%s' moved from %s to %s. Reason: %s",
                task.getTitle(), fromDate, toDate, reason);
    }
}

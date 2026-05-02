package com.aibert.dosw.domain.model.schedule;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a task assigned to a specific time block on a given day.
 * Produced by the distribution engine (R16).
 */
@Getter
@Builder
public class ScheduledBlock {
    private final PlanningTask task;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    /**
     * Calculates the duration of this scheduled block in hours.
     */
    public double getDurationHours() {
        int startMinutes = startTime.getHour() * 60 + startTime.getMinute();
        int endMinutes = endTime.getHour() * 60 + endTime.getMinute();
        return (endMinutes - startMinutes) / 60.0;
    }
}

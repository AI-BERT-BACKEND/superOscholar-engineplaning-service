package com.aibert.dosw.domain.model.schedule;

import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Available time block within a day.
 * Represents a specific interval where the student can study.
 */
@Getter
@Builder
public class TimeSlot {

    private final LocalTime startTime;
    private final LocalTime endTime;

    /**
     * Calculates the duration of the block in hours.
     *
     * @return duration in decimal hours
     */
    public double getDurationHours() {
        int startMinutes = startTime.getHour() * 60
                + startTime.getMinute();
        int endMinutes = endTime.getHour() * 60
                + endTime.getMinute();
        return (endMinutes - startMinutes) / 60.0;
    }

    /**
     * Checks whether this block can fit a given task duration.
     *
     * @param requiredHours required hours
     */
    public boolean canFit(double requiredHours) {
        return getDurationHours() >= requiredHours;
    }

    /**
     * Human-readable representation of the block.
     * Example: "14:00 - 17:00 (3.0h)"
     */
    @Override
    public String toString() {
        return String.format("%s - %s (%.1fh)",
                startTime, endTime, getDurationHours());
    }
}

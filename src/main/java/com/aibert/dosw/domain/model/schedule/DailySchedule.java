package com.aibert.dosw.domain.model.schedule;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Availability schedule for a student on a specific day.
 * Contains the free time blocks available for studying.
 */
@Getter
@Builder
public class DailySchedule {

    private final String userId;
    private final LocalDate date;
    private final double totalAvailableHours;
    private final List<TimeSlot> availableSlots;
    private final boolean isClassDay;

    /**
     * Checks whether the day has available time to study.
     */
    public boolean hasAvailableTime() {
        return totalAvailableHours > 0;
    }

    /**
     * Checks whether the day can fit a task of a given duration.
     *
     * @param requiredHours hours required by the task
     */
    public boolean canAccommodate(double requiredHours) {
        return totalAvailableHours >= requiredHours;
    }
}

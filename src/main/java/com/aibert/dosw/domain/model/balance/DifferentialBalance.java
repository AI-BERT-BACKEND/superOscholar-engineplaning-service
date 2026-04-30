package com.aibert.dosw.domain.model.balance;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents the differential balance for a specific day.
 * Computes the difference between available hours and scheduled hours.
 *
 * differential = availableHours - scheduledHours
 *
 * OVERLOADED -> differential < -1.0 (more tasks than time)
 * BALANCED -> differential between -1.0 and 2.0
 * FREE -> differential > 2.0 (free time available)
 */
@Getter
@Builder
public class DifferentialBalance {

    private final LocalDate date;
    private final double availableHours;
    private final double scheduledHours;
    private final double differential;
    private final BalanceStatus status;

    /**
     * Factory method that computes differential and status
     * from available and scheduled hours.
     *
     * @param date           day of the balance
     * @param availableHours free hours for that day
     * @param scheduledHours hours already assigned
     * @return DifferentialBalance with computed status
     */
    public static DifferentialBalance of(
            LocalDate date,
            double availableHours,
            double scheduledHours) {

        double differential = availableHours - scheduledHours;
        BalanceStatus status = computeStatus(differential);

        return DifferentialBalance.builder()
                .date(date)
                .availableHours(availableHours)
                .scheduledHours(scheduledHours)
                .differential(differential)
                .status(status)
                .build();
    }

    /**
     * Determines the balance status based on the differential.
     *
     * @param differential difference between available and scheduled
     * @return balance status
     */
    private static BalanceStatus computeStatus(double differential) {
        if (differential < -1.0)
            return BalanceStatus.OVERLOADED;
        if (differential > 2.0)
            return BalanceStatus.FREE;
        return BalanceStatus.BALANCED;
    }

    /**
     * Checks if the day is overloaded.
     */
    public boolean isOverloaded() {
        return this.status == BalanceStatus.OVERLOADED;
    }

    /**
     * Checks if the day has free time available.
     */
    public boolean hasFreeTime() {
        return this.status == BalanceStatus.FREE;
    }

    /**
     * Reduces available hours by assigning more tasks.
     * Returns a new DifferentialBalance instance.
     *
     * @param hours hours to reduce
     * @return updated balance with reduced availability
     */
    public DifferentialBalance reduceAvailability(double hours) {
        double newScheduled = this.scheduledHours + hours;
        return DifferentialBalance.of(this.date, this.availableHours, newScheduled);
    }
}

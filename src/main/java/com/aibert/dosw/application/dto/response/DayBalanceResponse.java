package com.aibert.dosw.application.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing the workload analysis for a single day (R15 weeklyLoadAnalysis).
 * Contains hours assigned, hours available, and occupancy percentage.
 */
@Getter
@Builder
public class DayBalanceResponse {

    /** The date of this day's analysis. */
    private final LocalDate date;

    /** Total hours available for study that day. */
    private final double availableHours;

    /** Total hours already assigned to tasks that day. */
    private final double assignedHours;

    /** Occupancy percentage: (assignedHours / availableHours) * 100. */
    private final double occupancyPercent;

    /** Balance status: OVERLOADED (>80%), FREE (<20%), or BALANCED. */
    private final String status;
}


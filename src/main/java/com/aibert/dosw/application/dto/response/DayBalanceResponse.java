package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing the workload analysis for a single day (R15
 * weeklyLoadAnalysis).
 * Contains hours assigned, hours available, and occupancy percentage.
 */
@Getter
@Builder
@Schema(description = "Daily workload analysis with availability, assignment, and status")
public class DayBalanceResponse {

    /** The date of this day's analysis. */
    @Schema(description = "Date of this daily analysis", example = "2026-05-13")
    private final LocalDate date;

    /** Total hours available for study that day. */
    @Schema(description = "Total hours available for study that day", example = "6.0")
    private final double availableHours;

    /** Total hours already assigned to tasks that day. */
    @Schema(description = "Total hours assigned to tasks that day", example = "4.5")
    private final double assignedHours;

    /** Occupancy percentage: (assignedHours / availableHours) * 100. */
    @Schema(description = "Occupancy percentage for the day", example = "75.0")
    private final double occupancyPercent;

    /** Balance status: OVERLOADED (>80%), FREE (<20%), or BALANCED. */
    @Schema(description = "Load status label", example = "BALANCED")
    private final String status;
}

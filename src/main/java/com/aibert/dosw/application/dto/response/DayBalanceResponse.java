package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing the workload analysis for a single day (AIB-23 / R15
 * weeklyLoadAnalysis).
 * Contains minutes assigned, minutes available, and occupancy percentage.
 */
@Getter
@Builder
@Schema(description = "Daily workload analysis with availability, assignment, and occupancy status")
public class DayBalanceResponse {

    @Schema(description = "Date of this daily analysis", example = "2026-05-13")
    private final LocalDate date;

    @Schema(description = "Total minutes available for study that day, from the student's profile schedule", example = "360")
    private final int availableMinutes;

    @Schema(description = "Total minutes assigned to tasks that day (sum of task durations)", example = "270")
    private final int assignedMinutes;

    @Schema(description = "Occupancy percentage: (assignedMinutes / availableMinutes) × 100", example = "75.0")
    private final double occupancyPercentage;

    @Schema(description = "Load status: OVERLOADED (>80%), FREE (<20%), or BALANCED", example = "BALANCED")
    private final String status;
}

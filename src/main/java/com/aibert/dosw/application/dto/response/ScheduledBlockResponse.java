package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing a successfully assigned time block.
 */
@Getter
@Builder
@Schema(description = "Assigned study block with task and time slot details")
public class ScheduledBlockResponse {
    @Schema(description = "Task scheduled in this time block")
    private final PrioritizedTaskResponse task;

    @Schema(description = "Date of the scheduled block", example = "2026-05-13")
    private final LocalDate date;

    @Schema(description = "Block start time", example = "14:00:00")
    private final LocalTime startTime;

    @Schema(description = "Block end time", example = "15:30:00")
    private final LocalTime endTime;

    @Schema(description = "Duration of the block in hours", example = "1.5")
    private final double durationHours;
}

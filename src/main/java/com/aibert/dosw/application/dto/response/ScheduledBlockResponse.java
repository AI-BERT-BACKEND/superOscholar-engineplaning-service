package com.aibert.dosw.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing a successfully assigned time block.
 */
@Getter
@Builder
public class ScheduledBlockResponse {
    private final PrioritizedTaskResponse task;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final double durationHours;
}

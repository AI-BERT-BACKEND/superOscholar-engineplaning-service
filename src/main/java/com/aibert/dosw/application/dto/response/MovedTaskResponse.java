package com.aibert.dosw.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a task that was relocated during dynamic rebalancing (R17).
 * Contains the original block and new block for frontend display.
 */
@Getter
@Builder
public class MovedTaskResponse {

    /** ID of the relocated task. */
    private final String taskId;

    /** Title of the relocated task. */
    private final String taskTitle;

    /** Original date before rebalancing. */
    private final LocalDate originalDate;

    /** Original start time before rebalancing. */
    private final LocalTime originalStartTime;

    /** New date after rebalancing. */
    private final LocalDate newDate;

    /** New start time after rebalancing. */
    private final LocalTime newStartTime;

    /** Human-readable reason for the relocation. */
    private final String reason;
}

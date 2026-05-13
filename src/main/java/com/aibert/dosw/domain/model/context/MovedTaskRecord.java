package com.aibert.dosw.domain.model.context;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Records a task that was relocated during a dynamic rebalancing operation (R17).
 *
 * <p>Tracks the original block (date + time) and the new block assigned to the task,
 * allowing the frontend to show exactly what changed and why.</p>
 */
@Getter
@Builder
public class MovedTaskRecord {

    /** ID of the relocated task. */
    private final String taskId;

    /** Title of the relocated task (for display). */
    private final String taskTitle;

    /** Original date of the task block before rebalancing. */
    private final LocalDate originalDate;

    /** Original start time of the block before rebalancing. */
    private final LocalTime originalStartTime;

    /** New date assigned to the task after rebalancing. */
    private final LocalDate newDate;

    /** New start time assigned to the task after rebalancing. */
    private final LocalTime newStartTime;

    /** Human-readable reason for the relocation. */
    private final String reason;
}

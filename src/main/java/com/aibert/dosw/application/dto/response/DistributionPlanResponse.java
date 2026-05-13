package com.aibert.dosw.application.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing the final weekly distribution plan.
 * It explicitly captures the "unassignedTasks" handling requirement.
 */
@Getter
@Builder
public class DistributionPlanResponse {
    private final String studentId;

    /**
     * Tasks that were successfully assigned to available time blocks.
     */
    private final List<ScheduledBlockResponse> assignedBlocks;

    /**
     * Tasks that could not be assigned because the available time
     * was insufficient to cover them before their deadlines.
     */
    private final List<PrioritizedTaskResponse> unassignedTasks;

    /**
     * Tasks relocated during a rebalancing (R17).
     * Each entry includes taskId, original block and new block.
     */
    private final List<MovedTaskResponse> movedTasks;

    /**
     * Tasks affected by rebalancing that have a deadline in less than 24h.
     * Flagged specifically to alert the frontend/user.
     */
    private final List<PrioritizedTaskResponse> criticalAlerts;

    private final boolean fullyAssigned;

    /**
     * Human-readable result message per spec:
     * "¡Plan de trabajo generado exitosamente!" or task-not-found warnings.
     */
    private final String message;
}


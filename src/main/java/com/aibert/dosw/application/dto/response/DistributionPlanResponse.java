package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing the final weekly distribution plan.
 * It explicitly captures the "unassignedTasks" handling requirement.
 */
@Getter
@Builder
@Schema(description = "Weekly distribution plan produced by the scheduling engine")
public class DistributionPlanResponse {
    @Schema(description = "Student identifier for the plan", example = "100095379")
    private final String studentId;

    /**
     * Tasks that were successfully assigned to available time blocks.
     */
    @Schema(description = "Blocks successfully scheduled for the week")
    private final List<ScheduledBlockResponse> assignedBlocks;

    /**
     * Tasks that could not be assigned because the available time
     * was insufficient to cover them before their deadlines.
     */
    @Schema(description = "Tasks that could not be scheduled within available time")
    private final List<PrioritizedTaskResponse> unassignedTasks;

    /**
     * Tasks relocated during a rebalancing (R17).
     * Each entry includes taskId, original block and new block.
     */
    @Schema(description = "Tasks moved during a rebalance")
    private final List<MovedTaskResponse> movedTasks;

    /**
     * Tasks affected by rebalancing that have a deadline in less than 24h.
     * Flagged specifically to alert the frontend/user.
     */
    @Schema(description = "Tasks with deadlines within 24 hours after rebalancing")
    private final List<PrioritizedTaskResponse> criticalAlerts;

    @Schema(description = "True when all tasks were scheduled", example = "true")
    private final boolean fullyAssigned;

    /**
     * AIB-27: Days where the proposed plan exceeded MAX_MINUTES_PER_DAY = 240
     * before the overload-protection cap was applied. Empty when no overload.
     */
    @Schema(description = "Days with overload detected before the daily cap was applied (AIB-27)")
    private final List<OverloadedDayResponse> overloadedDays;

    /**
     * Human-readable result message per spec:
     * "¡Plan de trabajo generado exitosamente!" or task-not-found warnings.
     */
    @Schema(description = "Human-readable result message", example = "Plan generated successfully")
    private final String message;
}

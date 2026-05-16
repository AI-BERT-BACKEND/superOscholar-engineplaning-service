package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a successfully assigned study block (AIB-24).
 * Contains flat task fields and the scheduled date-time of the session.
 */
@Getter
@Builder
@Schema(description = "Assigned study block with task reference and scheduled session details")
public class ScheduledBlockResponse {

    @Schema(description = "Identifier of the task assigned to this block", example = "task-abc-123")
    private final String taskId;

    @Schema(description = "Title of the task for display purposes", example = "Parcial de Cálculo")
    private final String title;

    @Schema(description = "Date and time of the study session (ISO 8601)", example = "2026-05-13T14:00:00")
    private final LocalDateTime scheduledDate;

    @Schema(description = "Duration of this study block in minutes. Daily total ≤ MAX_MINUTES_PER_DAY (240)", example = "120")
    private final int estimatedDurationMinutes;

    @Schema(description = "Priority of the task: LOW | MEDIUM | HIGH | CRITICAL", example = "HIGH")
    private final String priority;
}

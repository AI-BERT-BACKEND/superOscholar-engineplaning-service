package com.aibert.dosw.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for reporting a failed or incomplete study block.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Payload used to report a missed or failed study block for a student")
public class FailureReportRequest {
    @Schema(description = "Student identifier reporting the failure", example = "student-123")
    private String studentId;

    @Schema(description = "Identifier of the task that failed", example = "task-456")
    private String taskId;

    @Schema(description = "Date when the study block was missed", example = "2026-05-12")
    private LocalDate failedDate;

    @Schema(description = "Number of study hours missed in the block", example = "2.5")
    private double hoursMissed;

    @Schema(description = "Short reason or note for the failure", example = "Sick day")
    private String reason;
}

package com.aibert.dosw.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "studentId is required")
    @Schema(description = "Student identifier reporting the failure", example = "100095379")
    private String studentId;

    @NotBlank(message = "taskId is required")
    @Schema(description = "Identifier of the task that failed", example = "task-456")
    private String taskId;

    @JsonProperty("date")
    @NotNull(message = "date is required")
    @Schema(description = "Date when the study block was missed", example = "2026-05-12")
    private LocalDate failedDate;

    @JsonProperty("lostHours")
    @Positive(message = "lostHours must be greater than 0")
    @Schema(description = "Number of study hours missed in the block", example = "2.5")
    private double hoursMissed;

    @JsonProperty("failureReason")
    @Size(max = 500, message = "failureReason must not exceed 500 characters")
    @Schema(description = "Short reason or note for the failure", example = "Sick day")
    private String reason;
}

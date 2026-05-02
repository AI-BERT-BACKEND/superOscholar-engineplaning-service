package com.aibert.dosw.application.dto.request;

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
public class FailureReportRequest {
    private String studentId;
    private String taskId;
    private LocalDate failedDate;
    private double hoursMissed;
    private String reason;
}

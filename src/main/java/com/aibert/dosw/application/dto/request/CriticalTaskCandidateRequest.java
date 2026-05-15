package com.aibert.dosw.application.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriticalTaskCandidateRequest {
    private String taskId;
    private String title;
    private String subjectId;
    private String taskType;
    private LocalDateTime deadline;
    private LocalDateTime scheduledDate;
    private Integer estimatedDurationMinutes;
    private Double priorityScore;
    private String priorityLevel;
    private String status;
}

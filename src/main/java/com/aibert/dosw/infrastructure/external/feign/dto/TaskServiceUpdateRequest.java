package com.aibert.dosw.infrastructure.external.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskServiceUpdateRequest {
    private Integer estimatedDurationMinutes;
    private String priority;
}

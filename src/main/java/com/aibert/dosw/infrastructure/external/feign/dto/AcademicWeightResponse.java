package com.aibert.dosw.infrastructure.external.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicWeightResponse {
    private String subjectId;
    private double academicWeight; // 0.0 - 1.0
}

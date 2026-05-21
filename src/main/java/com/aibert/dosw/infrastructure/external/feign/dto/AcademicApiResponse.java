package com.aibert.dosw.infrastructure.external.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper that matches the ApiResponse envelope returned by academic-service.
 * academic-service returns: { "message": "...", "data": { "academicWeight": 0.35 }, ... }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicApiResponse {
    private String message;
    private AcademicWeightResponse data;
}

package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.AcademicWeightResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign Client para comunicarse con academic-service.
 */
@FeignClient(name = "academic-service", url = "${feign.academic-service.url}", fallback = AcademicServiceClientFallback.class)
public interface AcademicServiceClient {

    @GetMapping("/academic/weight")
    AcademicWeightResponse getAcademicWeight(
            @RequestParam("studentId") String studentId,
            @RequestParam("subjectId") String subjectId);
}

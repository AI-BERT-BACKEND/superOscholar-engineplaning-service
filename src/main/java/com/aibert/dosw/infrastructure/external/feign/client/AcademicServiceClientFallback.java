package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.AcademicWeightResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AcademicServiceClientFallback implements AcademicServiceClient {

    @Override
    public AcademicWeightResponse getAcademicWeight(String studentId, String subjectId) {
        log.warn("academic-service no disponible. Usando peso academico 0.0 para subjectId='{}'.", subjectId);
        return AcademicWeightResponse.builder()
                .subjectId(subjectId)
                .academicWeight(0.0)
                .build();
    }
}

package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.ports.out.AcademicWeightProviderPort;
import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import com.aibert.dosw.infrastructure.external.feign.client.AcademicServiceClient;
import com.aibert.dosw.infrastructure.external.feign.dto.AcademicApiResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AcademicServiceAdapter implements AcademicWeightProviderPort {

    private final AcademicServiceClient academicServiceClient;

    @Override
    public Optional<AcademicWeight> getAcademicWeight(String studentId, String subjectId) {
        if (subjectId == null || subjectId.isBlank()) {
            return Optional.empty();
        }
        AcademicApiResponse response = academicServiceClient.getAcademicWeight(studentId, subjectId);
        if (response == null || response.getData() == null) {
            return Optional.empty();
        }
        return Optional.of(AcademicWeight.of(subjectId, response.getData().getAcademicWeight()));
    }
}

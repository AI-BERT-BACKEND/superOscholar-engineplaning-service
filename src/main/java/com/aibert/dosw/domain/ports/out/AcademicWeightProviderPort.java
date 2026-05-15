package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import java.util.Optional;

/**
 * Output port to retrieve academic weight from academic-service.
 */
public interface AcademicWeightProviderPort {

    Optional<AcademicWeight> getAcademicWeight(String studentId, String subjectId);
}

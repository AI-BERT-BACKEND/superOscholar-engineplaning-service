package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.AcademicWeightResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcademicServiceClientFallbackTest {

    private final AcademicServiceClientFallback fallback = new AcademicServiceClientFallback();

    @Test
    void getAcademicWeight_returnsDefaultWeightWithSubjectId() {
        AcademicWeightResponse response = fallback.getAcademicWeight("student1", "MATH101");

        assertNotNull(response);
        assertEquals("MATH101", response.getSubjectId());
        assertEquals(0.0, response.getAcademicWeight());
    }

    @Test
    void getAcademicWeight_preservesSubjectIdInResponse() {
        AcademicWeightResponse response = fallback.getAcademicWeight("student1", "CS202");

        assertNotNull(response);
        assertEquals("CS202", response.getSubjectId());
    }

    @Test
    void getAcademicWeight_withNullSubjectId_returnsNullSubjectId() {
        AcademicWeightResponse response = fallback.getAcademicWeight("student1", null);

        assertNotNull(response);
        assertNull(response.getSubjectId());
        assertEquals(0.0, response.getAcademicWeight());
    }
}

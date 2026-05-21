package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.AcademicApiResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcademicServiceClientFallbackTest {

    private final AcademicServiceClientFallback fallback = new AcademicServiceClientFallback();

    @Test
    void getAcademicWeight_returnsDefaultWeightWithSubjectId() {
        AcademicApiResponse response = fallback.getAcademicWeight("student1", "MATH101");

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("MATH101", response.getData().getSubjectId());
        assertEquals(0.0, response.getData().getAcademicWeight());
    }

    @Test
    void getAcademicWeight_preservesSubjectIdInResponse() {
        AcademicApiResponse response = fallback.getAcademicWeight("student1", "CS202");

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("CS202", response.getData().getSubjectId());
    }

    @Test
    void getAcademicWeight_withNullSubjectId_returnsNullSubjectId() {
        AcademicApiResponse response = fallback.getAcademicWeight("student1", null);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertNull(response.getData().getSubjectId());
        assertEquals(0.0, response.getData().getAcademicWeight());
    }
}

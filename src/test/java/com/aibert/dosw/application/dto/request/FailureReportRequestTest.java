package com.aibert.dosw.application.dto.request;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FailureReportRequestTest {

    @Test
    void shouldSetAndGetAllFields() {
        FailureReportRequest request = new FailureReportRequest();
        request.setStudentId("st1");
        request.setTaskId("t1");
        request.setFailedDate(LocalDate.of(2026, 5, 5));
        request.setHoursMissed(2.5);
        request.setReason("Too tired");

        assertEquals("st1", request.getStudentId());
        assertEquals("t1", request.getTaskId());
        assertEquals(LocalDate.of(2026, 5, 5), request.getFailedDate());
        assertEquals(2.5, request.getHoursMissed());
        assertEquals("Too tired", request.getReason());
    }

    @Test
    void shouldHaveDefaults() {
        FailureReportRequest request = new FailureReportRequest();
        assertNull(request.getStudentId());
        assertNull(request.getTaskId());
        assertNull(request.getFailedDate());
        assertEquals(0.0, request.getHoursMissed());
        assertNull(request.getReason());
    }
}

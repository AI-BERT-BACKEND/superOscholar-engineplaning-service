package com.aibert.dosw.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanningDomainExceptionTest {

    @Test
    void shouldCreateWithMessageAndErrorCode() {
        PlanningDomainException ex = new PlanningDomainException("Test message", "ERR_001");

        assertEquals("Test message", ex.getMessage());
        assertEquals("ERR_001", ex.getErrorCode());
    }

    @Test
    void shouldCreateWithMessageErrorCodeAndCause() {
        RuntimeException cause = new RuntimeException("root cause");
        PlanningDomainException ex = new PlanningDomainException("Test message", "ERR_002", cause);

        assertEquals("Test message", ex.getMessage());
        assertEquals("ERR_002", ex.getErrorCode());
        assertEquals(cause, ex.getCause());
    }
}

package com.aibert.dosw.entrypoints.support;

import com.aibert.dosw.domain.exceptions.PlanningDomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentIdValidatorTest {

    @Test
    void validate_validUuid_doesNotThrow() {
        assertDoesNotThrow(() -> StudentIdValidator.validate("550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    void validate_null_throwsPlanningDomainException() {
        PlanningDomainException ex = assertThrows(
                PlanningDomainException.class,
                () -> StudentIdValidator.validate(null));
        assertEquals("INVALID_STUDENT_ID", ex.getErrorCode());
    }

    @Test
    void validate_blank_throwsPlanningDomainException() {
        PlanningDomainException ex = assertThrows(
                PlanningDomainException.class,
                () -> StudentIdValidator.validate("  "));
        assertEquals("INVALID_STUDENT_ID", ex.getErrorCode());
    }

    @Test
    void validate_invalidUuid_throwsPlanningDomainException() {
        PlanningDomainException ex = assertThrows(
                PlanningDomainException.class,
                () -> StudentIdValidator.validate("not-a-uuid"));
        assertEquals("INVALID_STUDENT_ID", ex.getErrorCode());
        assertTrue(ex.getMessage().contains("UUID"));
    }
}

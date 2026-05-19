package com.aibert.dosw.entrypoints.support;

import com.aibert.dosw.domain.exceptions.PlanningDomainException;
import java.util.UUID;

/**
 * Utility to validate that a studentId conforms to the UUID format
 * required by AIB-22 (RFC 4122).
 */
public final class StudentIdValidator {

    private StudentIdValidator() {
    }

    /**
     * Throws {@link PlanningDomainException} (HTTP 400) when {@code studentId}
     * is null, blank, or not a valid UUID string.
     *
     * @param studentId the value to validate
     */
    public static void validate(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new PlanningDomainException(
                    "El studentId es obligatorio y debe ser un UUID válido.",
                    "INVALID_STUDENT_ID");
        }
        try {
            UUID.fromString(studentId);
        } catch (IllegalArgumentException e) {
            throw new PlanningDomainException(
                    "El studentId debe tener formato UUID (ej. '550e8400-e29b-41d4-a716-446655440000'). Valor recibido: '"
                            + studentId + "'",
                    "INVALID_STUDENT_ID");
        }
    }
}

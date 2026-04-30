package com.aibert.dosw.domain.exceptions;

/**
 * Base exception for the planning domain.
 * All domain exceptions should extend from this class.
 */
public class PlanningDomainException extends RuntimeException {

    private final String errorCode;

    public PlanningDomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PlanningDomainException(String message,
            String errorCode,
            Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

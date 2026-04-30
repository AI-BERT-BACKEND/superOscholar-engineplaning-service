package com.aibert.dosw.entrypoints.advice;

import com.aibert.dosw.domain.exceptions.NoAvailabilityException;
import com.aibert.dosw.domain.exceptions.OverloadException;
import com.aibert.dosw.domain.exceptions.PlanningDomainException;
import com.aibert.dosw.domain.exceptions.TaskNotPlannableException;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for all controllers.
 * Converts domain exceptions into standardized HTTP responses.
 */
@RestControllerAdvice
@Slf4j
public class PlanningExceptionHandler {

    /**
     * Handles days without availability (409 Conflict).
     */
    @ExceptionHandler(NoAvailabilityException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoAvailability(
            NoAvailabilityException ex,
            HttpServletRequest request) {

        log.warn("Sin disponibilidad: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    /**
     * Handles overloaded days (422 Unprocessable Entity).
     */
    @ExceptionHandler(OverloadException.class)
    public ResponseEntity<ApiResponse<Void>> handleOverload(
            OverloadException ex,
            HttpServletRequest request) {

        log.warn("Sobrecarga detectada: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    /**
     * Handles non-plannable tasks (400 Bad Request).
     */
    @ExceptionHandler(TaskNotPlannableException.class)
    public ResponseEntity<ApiResponse<Void>> handleTaskNotPlannable(
            TaskNotPlannableException ex,
            HttpServletRequest request) {

        log.warn("Tarea no planificable: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    /**
     * Handles generic domain exceptions (400 Bad Request).
     */
    @ExceptionHandler(PlanningDomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(
            PlanningDomainException ex,
            HttpServletRequest request) {

        log.warn("Domain error [{}]: {}",
                ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    /**
     * Handles field validation errors (400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Validation error: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(
                        "Error en los datos enviados",
                        errors,
                        request.getRequestURI()));
    }

    /**
     * Handles any uncaught exception (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error at: {}", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Error interno del servidor. Intenta de nuevo.",
                        request.getRequestURI()));
    }
}

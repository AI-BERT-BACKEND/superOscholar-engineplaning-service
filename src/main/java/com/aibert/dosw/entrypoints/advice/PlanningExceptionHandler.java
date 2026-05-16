package com.aibert.dosw.entrypoints.advice;

import com.aibert.dosw.domain.exceptions.PlanningDomainException;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
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
         * Handles generic domain exceptions (400 Bad Request).
         */
        @ExceptionHandler(PlanningDomainException.class)
        public ResponseEntity<ApiResponse<Void>> handleDomainException(
                        PlanningDomainException ex,
                        HttpServletRequest request) {

                log.warn("Error de dominio [{}]: {}",
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
                                .toList();

                log.warn("Error de validación: {}", errors);
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

                log.error("Error inesperado en: {}", request.getRequestURI(), ex);
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(
                                                "Error interno del servidor. Intenta de nuevo.",
                                                request.getRequestURI()));
        }

        /**
         * Handles authorization errors (403 Forbidden).
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
                        AccessDeniedException ex,
                        HttpServletRequest request) {

                log.warn("Acceso denegado en {}: {}", request.getRequestURI(), ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(
                                                "No tienes permisos para acceder a este recurso.",
                                                request.getRequestURI()));
        }
}

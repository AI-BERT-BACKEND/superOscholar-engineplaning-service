package com.aibert.dosw.entrypoints.advice;

import com.aibert.dosw.domain.exceptions.PlanningDomainException;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

                log.warn("Error de dominio [codigo={}] en {}: {}",
                                ex.getErrorCode(), sl(request.getRequestURI()), ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }

        /**
         * Handles field validation errors from @Valid @RequestBody (400 Bad Request).
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

                log.warn("Error de validación de campos en {}: {}", sl(request.getRequestURI()), errors);
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.validationError(
                                                "Error en los datos enviados",
                                                errors,
                                                request.getRequestURI()));
        }

        /**
         * Handles constraint violations from @Validated on path/query params (400).
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
                        ConstraintViolationException ex,
                        HttpServletRequest request) {

                List<String> errors = ex.getConstraintViolations().stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .toList();

                log.warn("Violación de restricción en {}: {}", sl(request.getRequestURI()), errors);
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.validationError(
                                                "Parámetros de solicitud inválidos",
                                                errors,
                                                request.getRequestURI()));
        }

        /**
         * Handles missing required request headers (400 Bad Request).
         * Triggered when X-User-Id or any required header is absent.
         */
        @ExceptionHandler(MissingRequestHeaderException.class)
        public ResponseEntity<ApiResponse<Void>> handleMissingHeader(
                        MissingRequestHeaderException ex,
                        HttpServletRequest request) {

                log.warn("Header obligatorio ausente en {}: '{}'",
                                sl(request.getRequestURI()), ex.getHeaderName());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(
                                                "El header '" + ex.getHeaderName() + "' es obligatorio.",
                                                request.getRequestURI()));
        }

        /**
         * Handles malformed JSON request bodies (400 Bad Request).
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Void>> handleUnreadable(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {

                log.warn("Cuerpo de solicitud ilegible en {}: {}", sl(request.getRequestURI()), ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(
                                                "El cuerpo de la solicitud no es JSON válido o contiene tipos de datos incorrectos.",
                                                request.getRequestURI()));
        }

        /**
         * Handles type mismatch for @RequestParam / @PathVariable (400 Bad Request).
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
                        MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {

                String message = String.format("El parámetro '%s' tiene un formato inválido: '%s'",
                                ex.getName(), ex.getValue());
                log.warn("Error de tipo de parámetro en {}: {}", sl(request.getRequestURI()), message);
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(message, request.getRequestURI()));
        }

        /**
         * Handles Feign client failures when connecting to external services (502/503).
         * Triggered when task-service, profile-service or academic-service are
         * unreachable
         * and no fallback could recover the call.
         */
        @ExceptionHandler(FeignException.class)
        public ResponseEntity<ApiResponse<Void>> handleFeignException(
                        FeignException ex,
                        HttpServletRequest request) {

                log.error("Error en llamada a servicio externo [status={}] en {}: {}",
                                ex.status(), sl(request.getRequestURI()), ex.getMessage());

                // Negative status (-1) or 5xx from downstream → 502 Bad Gateway
                HttpStatus status = (ex.status() < 0 || ex.status() >= 500)
                                ? HttpStatus.BAD_GATEWAY
                                : HttpStatus.SERVICE_UNAVAILABLE;

                return ResponseEntity
                                .status(status)
                                .body(ApiResponse.error(
                                                "No fue posible conectar con un servicio externo. Intenta de nuevo más tarde.",
                                                request.getRequestURI()));
        }

        /**
         * Handles authorization errors (403 Forbidden).
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
                        AccessDeniedException ex,
                        HttpServletRequest request) {

                log.warn("Acceso denegado en {}: {}", sl(request.getRequestURI()), ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(
                                                "No tienes permisos para acceder a este recurso.",
                                                request.getRequestURI()));
        }

        /**
         * Catch-all handler for any uncaught exception (500 Internal Server Error).
         * Logs the full stack trace for bug detection.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGeneral(
                        Exception ex,
                        HttpServletRequest request) {

                log.error("Error inesperado no controlado en {}", sl(request.getRequestURI()), ex);
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(
                                                "Error interno del servidor. Intenta de nuevo.",
                                                request.getRequestURI()));
        }

        private static String sl(String s) {
                return s == null ? "" : s.replaceAll("[\r\n]", "_");
        }
}

package com.aibert.dosw.entrypoints.advice;

import com.aibert.dosw.domain.exceptions.PlanningDomainException;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import feign.FeignException;
import feign.Request;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlanningExceptionHandlerTest {

    private final PlanningExceptionHandler handler = new PlanningExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    void shouldHandleDomainException() {
        request.setRequestURI("/api/test");
        PlanningDomainException ex = new PlanningDomainException("Domain error occurred", "TEST_ERROR");

        ResponseEntity<ApiResponse<Void>> response = handler.handleDomainException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Void> body = Objects.requireNonNull(response.getBody());
        assertEquals("Domain error occurred", body.getMessage());
    }

    @Test
    void shouldHandleValidationException() throws Exception {
        request.setRequestURI("/api/test");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("objectName", "field", "must not be null")));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                new MethodParameter(
                        Objects.requireNonNull(
                                PlanningExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class)),
                        0),
                bindingResult);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<Void> body = Objects.requireNonNull(response.getBody());
        assertEquals("Error en los datos enviados", body.getMessage());
    }

    @Test
    void shouldHandleConstraintViolation() {
        request.setRequestURI("/api/test");
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("studentId");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ApiResponse<Void>> response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Parámetros de solicitud inválidos", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void shouldHandleMissingHeader() throws Exception {
        request.setRequestURI("/planning/prioritization");
        MissingRequestHeaderException ex = new MissingRequestHeaderException("X-Student-Id",
                new MethodParameter(
                        PlanningExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class), 0));

        ResponseEntity<ApiResponse<Void>> response = handler.handleMissingHeader(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("X-Student-Id"));
    }

    @Test
    void shouldHandleHttpMessageNotReadable() {
        request.setRequestURI("/planning/distribution");
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("JSON parse error");

        ResponseEntity<ApiResponse<Void>> response = handler.handleUnreadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("JSON válido"));
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatch() throws Exception {
        request.setRequestURI("/planning/test");
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("page");
        when(ex.getValue()).thenReturn("abc");

        ResponseEntity<ApiResponse<Void>> response = handler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("page"));
    }

    @Test
    void shouldHandleFeignException5xx() {
        request.setRequestURI("/planning/distribution");
        Request feignRequest = Request.create(Request.HttpMethod.GET, "http://task-service/tasks",
                Map.of(), null, StandardCharsets.UTF_8, null);
        FeignException ex = FeignException.errorStatus("GET", feign.Response.builder()
                .status(503).reason("Service Unavailable").request(feignRequest)
                .headers(Map.of()).body(new byte[0]).build());

        ResponseEntity<ApiResponse<Void>> response = handler.handleFeignException(ex, request);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("servicio externo"));
    }

    @Test
    void shouldHandleFeignException4xx() {
        request.setRequestURI("/planning/prioritization");
        Request feignRequest = Request.create(Request.HttpMethod.GET, "http://task-service/tasks",
                Map.of(), null, StandardCharsets.UTF_8, null);
        FeignException ex = FeignException.errorStatus("GET", feign.Response.builder()
                .status(404).reason("Not Found").request(feignRequest)
                .headers(Map.of()).body(new byte[0]).build());

        ResponseEntity<ApiResponse<Void>> response = handler.handleFeignException(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    void shouldHandleGenericException() {
        request.setRequestURI("/api/test");
        Exception ex = new Exception("Internal error");

        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<Void> body = Objects.requireNonNull(response.getBody());
        assertEquals("Error interno del servidor. Intenta de nuevo.", body.getMessage());
    }

    @Test
    void shouldHandleAccessDeniedException() {
        request.setRequestURI("/planning/prioritization");
        AccessDeniedException ex = new AccessDeniedException("studentId does not match authenticated user");

        ResponseEntity<ApiResponse<Void>> response = handler.handleAccessDenied(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ApiResponse<Void> body = Objects.requireNonNull(response.getBody());
        assertEquals("No tienes permisos para acceder a este recurso.", body.getMessage());
        assertEquals("/planning/prioritization", body.getPath());
    }

    @SuppressWarnings("unused")
    private void dummy(String value) {
        assertNotNull(value);
    }
}

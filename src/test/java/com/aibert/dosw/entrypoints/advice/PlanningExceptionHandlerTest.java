package com.aibert.dosw.entrypoints.advice;

import com.aibert.dosw.domain.exceptions.PlanningDomainException;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlanningExceptionHandlerTest {

    private final PlanningExceptionHandler handler = new PlanningExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    void shouldHandleDomainException() {
        request.setRequestURI("/api/test");
        PlanningDomainException ex = new PlanningDomainException("Domain error occurred", "TEST_ERROR");
        
        ResponseEntity<ApiResponse<Void>> response = handler.handleDomainException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Domain error occurred", response.getBody().getMessage());
    }

    @Test
    void shouldHandleValidationException() {
        request.setRequestURI("/api/test");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("objectName", "field", "must not be null")));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error en los datos enviados", response.getBody().getMessage());
    }

    @Test
    void shouldHandleGenericException() {
        request.setRequestURI("/api/test");
        Exception ex = new Exception("Internal error");
        
        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error interno del servidor. Intenta de nuevo.", response.getBody().getMessage());
    }
}

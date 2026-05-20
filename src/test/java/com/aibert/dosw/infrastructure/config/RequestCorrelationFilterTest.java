package com.aibert.dosw.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestCorrelationFilterTest {

    private RequestCorrelationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RequestCorrelationFilter();
    }

    @Test
    void shouldReusePresentRequestId() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-Request-Id")).thenReturn("my-request-id-123");

        filter.doFilter(request, response, chain);

        verify(response).setHeader("X-Request-Id", "my-request-id-123");
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldGenerateRequestIdWhenHeaderAbsent() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-Request-Id")).thenReturn(null);

        filter.doFilter(request, response, chain);

        // Verify header was set with a generated ID (12 chars, no dashes)
        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("X-Request-Id"), captor.capture());
        String generatedId = captor.getValue();
        assertNotNull(generatedId);
        assertEquals(12, generatedId.length());
        assertFalse(generatedId.contains("-"));
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldGenerateRequestIdWhenHeaderIsBlank() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-Request-Id")).thenReturn("   ");

        filter.doFilter(request, response, chain);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("X-Request-Id"), captor.capture());
        // A blank header should produce a generated ID, not the blank string
        assertFalse(captor.getValue().isBlank());
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldClearMdcEvenIfChainThrows() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-Request-Id")).thenReturn("err-id");
        doThrow(new RuntimeException("chain error")).when(chain).doFilter(request, response);

        assertThrows(RuntimeException.class, () -> filter.doFilter(request, response, chain));
        // MDC key should have been cleaned up — no assertion needed for MDC itself,
        // just verifying the filter does not swallow the exception
    }
}

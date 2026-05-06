package com.aibert.dosw.infrastructure.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.mock.web.MockFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetAuthenticationWhenTokenValid() throws Exception {
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        when(tokenProvider.validateToken("good-token")).thenReturn(true);
        when(tokenProvider.getUsernameFromToken("good-token")).thenReturn("st1");

        OncePerRequestFilter filter = new JwtAuthenticationFilter(tokenProvider);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer good-token");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("st1", authentication.getName());
    }

    @Test
    void shouldIgnoreWhenTokenInvalid() throws Exception {
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        when(tokenProvider.validateToken("bad-token")).thenReturn(false);

        OncePerRequestFilter filter = new JwtAuthenticationFilter(tokenProvider);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer bad-token");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldIgnoreWhenNoAuthorizationHeader() throws Exception {
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);

        OncePerRequestFilter filter = new JwtAuthenticationFilter(tokenProvider);

        MockHttpServletRequest request = new MockHttpServletRequest();
        // No Authorization header set

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldIgnoreWhenAuthorizationHeaderNotBearer() throws Exception {
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);

        OncePerRequestFilter filter = new JwtAuthenticationFilter(tokenProvider);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic some-credentials");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueFilterChainWhenExceptionThrown() throws Exception {
        JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
        when(tokenProvider.validateToken("error-token")).thenThrow(new RuntimeException("Unexpected error"));

        OncePerRequestFilter filter = new JwtAuthenticationFilter(tokenProvider);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer error-token");

        // Should not throw; the filter chain continues
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

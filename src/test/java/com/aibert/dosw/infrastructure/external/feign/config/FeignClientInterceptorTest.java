package com.aibert.dosw.infrastructure.external.feign.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeignClientInterceptorTest {

    @Test
    void shouldForwardAuthorizationHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        RequestTemplate template = new RequestTemplate();

        new FeignClientInterceptor().apply(template);

        assertEquals("Bearer token", template.headers().get(HttpHeaders.AUTHORIZATION).iterator().next());
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldSkipWhenNoRequestAttributes() {
        RequestContextHolder.resetRequestAttributes();
        RequestTemplate template = new RequestTemplate();

        new FeignClientInterceptor().apply(template);

        assertNull(template.headers().get(HttpHeaders.AUTHORIZATION));
    }
}

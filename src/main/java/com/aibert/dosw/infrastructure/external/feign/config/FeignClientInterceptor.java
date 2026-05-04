package com.aibert.dosw.infrastructure.external.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Global Feign Interceptor.
 * Automatically extracts the JWT Bearer token from the incoming HTTP request
 * and injects it into all outgoing Feign requests to other microservices.
 */
@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            
            // Forward the Authorization header if it exists
            if (authorizationHeader != null) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
            }
        }
    }
}

package com.aibert.dosw.infrastructure.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Servlet filter that assigns a correlation ID to every incoming HTTP request.
 *
 * <p>
 * If the caller includes an {@code X-Request-Id} header, that value is reused;
 * otherwise a short UUID is generated. The ID is stored in the SLF4J MDC under
 * the key {@code requestId} so that every log line emitted during the request
 * carries it automatically (requires a logging pattern that references
 * {@code %X{requestId}}).
 *
 * <p>
 * The same ID is echoed back to the caller via the {@code X-Request-Id}
 * response header, enabling end-to-end traceability across service calls.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCorrelationFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestId = StringUtils.hasText(httpRequest.getHeader(REQUEST_ID_HEADER))
                ? httpRequest.getHeader(REQUEST_ID_HEADER)
                : UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        MDC.put(MDC_KEY, requestId);
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}

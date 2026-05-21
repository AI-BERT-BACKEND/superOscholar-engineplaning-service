package com.aibert.dosw.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

/**
 * Global Security Configuration for the planning-service.
 *
 * <p>
 * When {@code planning.security.enabled=true} (default), all {@code /planning/**}
 * endpoints require a valid JWT in the {@code Authorization} header.
 *
 * <p>
 * When {@code planning.security.enabled=false} (local/test profile only),
 * JWT validation is skipped and a synthetic {@code Authentication} is derived
 * from the {@code X-User-Id} request header so that {@code authentication.getName()}
 * returns the userId correctly without a real token.
 * <b>Never set this to {@code false} in production or QA environments.</b>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Set to false only in the local profile to bypass JWT for Swagger testing. */
    @Value("${planning.security.enabled:true}")
    private boolean securityEnabled;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            @Qualifier("infrastructureJwtAuthenticationFilter") JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF is intentionally disabled: this service is a stateless REST API
        // authenticated via JWT Bearer tokens (not cookies), so CSRF attacks do not apply.
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (!securityEnabled) {
            // Local-only: derive authentication from X-User-Id header so that
            // authentication.getName() returns the userId correctly in Swagger testing.
            http
                    .addFilterBefore(this::localStudentIdAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                            .requestMatchers("/actuator/health/**").permitAll()
                            .requestMatchers("/planning/**").authenticated()
                            .anyRequest().authenticated())
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    /**
     * Inline filter used ONLY when security is disabled (local/test profile).
     * Sets a synthetic Authentication whose {@code getName()} returns the
     * {@code X-User-Id} header value so controllers can call
     * {@code authentication.getName()} without a real JWT.
     */
    private void localStudentIdAuthFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            jakarta.servlet.FilterChain chain) throws java.io.IOException, jakarta.servlet.ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        String userId = httpReq.getHeader("X-User-Id");
        if (StringUtils.hasText(userId)) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null,
                    List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}

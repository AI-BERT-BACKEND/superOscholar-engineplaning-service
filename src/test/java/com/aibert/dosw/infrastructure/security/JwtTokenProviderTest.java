package com.aibert.dosw.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    @Test
    void shouldValidateTokenAndExtractUsername() {
        String secret = "test-jwt-secret-for-tests-32-bytes";

        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", secret);
        provider.init();

        String token = Jwts.builder()
                .setSubject("student1")
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertTrue(provider.validateToken(token));
        assertEquals("student1", provider.getUsernameFromToken(token));
        assertFalse(provider.validateToken("invalid.token"));
    }
}

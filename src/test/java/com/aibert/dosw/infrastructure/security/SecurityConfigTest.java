package com.aibert.dosw.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "feign.task-service.url=http://localhost:8084",
        "feign.profile-service.url=http://localhost:8082",
        "feign.academic-service.url=http://localhost:8083",
        "feign.recommendation-service.url=http://localhost:8086",
        "feign.planning-service.url=http://localhost:1505",
        "jwt.secret=VmVyeVNlY3JldEtleUZvckFJQmVydEFwcGxpY2F0aW9uQXV0aGVudGljYXRpb24="
})
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void shouldLoadSecurityFilterChain() {
        assertNotNull(securityFilterChain);
    }
}

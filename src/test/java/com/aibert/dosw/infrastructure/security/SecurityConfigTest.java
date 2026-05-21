package com.aibert.dosw.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {
        "feign.task-service.url=http://localhost:8084",
        "feign.profile-service.url=http://localhost:8082",
        "feign.academic-service.url=http://localhost:8083",
        "feign.recommendation-service.url=http://localhost:8086",
        "feign.planning-service.url=http://localhost:1505",
        "jwt.secret=VmVyeVNlY3JldEtleUZvckFJQmVydEFwcGxpY2F0aW9uQXV0aGVudGljYXRpb24=",
        "planning.security.enabled=false"
})
@AutoConfigureMockMvc
class SecurityConfigLocalModeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldPermitSwaggerDocsWhenSecurityDisabled() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotReturn401WhenXUserIdHeaderProvided() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                        .header("X-User-Id", "550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(status().isOk());
    }
}

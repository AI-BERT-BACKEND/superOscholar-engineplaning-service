package com.aibert.dosw.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        private static final String SECURITY_SCHEME_NAME = "bearerAuth";

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Engine Planning Service API — AIBERT")
                                                .version("v1")
                                                .description("""
                                                                Academic planning engine for AIBERT — computes priority scores, detects academic risk, adjusts effort estimates, balances weekly workload, and distributes study tasks across the student's available time.

                                                                **Key responsibilities:**
                                                                - Task prioritization and ranking by priority score and deadline (AIB-22)
                                                                - Critical task recommendation: up to 3 urgent tasks within 48 h (AIB-22.2)
                                                                - High-risk detection: insufficient available time vs. estimated duration (AIB-22.3)
                                                                - Automatic estimation adjustment using a per-student correction factor (AIB-22.4)
                                                                - Weekly workload balance analysis and redistribution suggestions (AIB-23, R15)
                                                                - Dynamic rebalancing when a study block is missed or fails (R17)
                                                                - Automatic weekly task distribution into available time slots (AIB-24, R16)
                                                                - Internal task-change event reception to trigger background reprioritization (AIB-22.1)

                                                                **Kafka topics published:**
                                                                - `planning.notifications` — STUDY_SUGGESTION and OVERLOAD_ALERT events sent to the notification service when critical tasks or risk conditions are detected.

                                                                **Authentication:** all endpoints require a Bearer JWT token issued by the auth service. Use the Authorize button to set your token.

                                                                **Local testing:** see `swagger-tests/swagger-tests-guide.md` in the repository for ready-to-paste curl commands and a JWT token generation guide.

                                                                **Contact:** AI-BERT Backend Team
                                                                """)
                                                .contact(new Contact().name("AI-BERT Backend")))
                                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                                .components(new Components()
                                                .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                                                .name(SECURITY_SCHEME_NAME)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("JWT required in production/dev. Not needed in local profile.")));
        }

        /**
         * Injects an example value into the X-User-Id header of every operation
         * so Swagger UI pre-fills it and simplifies local testing.
         */
        @Bean
        public OperationCustomizer studentIdHeaderExample() {
                return (operation, handlerMethod) -> {
                        if (operation.getParameters() != null) {
                                operation.getParameters().stream()
                                                .filter(p -> "X-User-Id".equals(p.getName()))
                                                .forEach(p -> p.setExample("550e8400-e29b-41d4-a716-446655440000"));
                        }
                        return operation;
                };
        }
}

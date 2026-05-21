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
                                                .title("Engine Planning Service API")
                                                .version("v1")
                                                .description("""
                                                                Academic planning engine API.

                                                                **Local testing without authentication:**
                                                                Start with the `local` profile (`--spring.profiles.active=local`).
                                                                No JWT token required. Just include the `X-User-Id` header
                                                                with any student UUID (e.g. `550e8400-e29b-41d4-a716-446655440000`).
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

package com.aibert.dosw.config;

import com.aibert.dosw.domain.ports.out.AiProviderPort;
import com.aibert.dosw.infrastructure.external.ai.gemini.GeminiAdapter;
import com.aibert.dosw.infrastructure.external.ai.groq.GroqAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
/**
 * Configuration for the active AI provider.
 * Selected via the ai.provider property.
 *
 * Supported values:
 * gemini -> Google Gemini 2.5 Flash (primary)
 * groq -> Groq llama-3.1-8b-instant (fallback)
 */
public class AiProviderConfig {

    // Enables GeminiAdapter when ai.provider=gemini.
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "gemini")
    public AiProviderPort geminiAiProvider(
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        return new GeminiAdapter(restTemplate, objectMapper);
    }

    // Enables GroqAdapter when ai.provider=groq.
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "groq")
    public AiProviderPort groqAiProvider(RestTemplate restTemplate) {
        return new GroqAdapter(restTemplate);
    }
}

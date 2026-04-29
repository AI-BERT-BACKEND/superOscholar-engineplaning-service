package com.aibert.dosw.infrastructure.external.ai.groq;

import com.aibert.dosw.domain.model.context.PlanningContext;
import com.aibert.dosw.domain.ports.out.AiProviderPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Adapter for the Groq API.
 * Active when ai.provider=groq in application.yml.
 * Serves as a fallback for Gemini.
 */
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "groq")
@Slf4j
public class GroqAdapter implements AiProviderPort {

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final RestTemplate restTemplate;

    @Value("${ai.groq.api-key}")
    private String apiKey;

    @Value("${ai.groq.model:llama-3.1-8b-instant}")
    private String model;

    public GroqAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = "aiProvider", fallbackMethod = "fallback")
    public String getSuggestion(PlanningContext context) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system",
                                "content", "You are AI.BERT. Always respond with valid JSON."),
                        Map.of("role", "user",
                                "content", buildPrompt(context))),
                "temperature", 0.3,
                "max_tokens", 300);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    GROQ_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null) {
                return extractContent(response.getBody());
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                log.warn("Groq rate limit reached");
            } else {
                log.error("Groq error: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected Groq error: {}", e.getMessage());
        }

        return fallback(context, null);
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> body) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            log.error("Error parsing Groq response", e);
            return "[]";
        }
    }

    private String buildPrompt(PlanningContext context) {
        return String.format("""
                University student needs planning assistance.
                At-risk subjects: %s
                Available hours today: %.1f
                Calculated plan: %s

                Respond with JSON:
                {"mensaje_principal": "...", "sugerencias": [
                  {"tipo": "STUDY_TODAY|RESCHEDULE|ALERT|REST",
                   "texto": "..."}
                ]}
                """,
                context.getRiskySummary(),
                context.getAvailableHoursToday(),
                context.getDailyPlanSummary());
    }

    public String fallback(PlanningContext context, Exception ex) {
        log.info("Using Java fallback in GroqAdapter");
        return """
                {
                  "mensaje_principal": "Review your most urgent tasks.",
                  "sugerencias": [
                	{"tipo": "STUDY_TODAY",
                	 "texto": "Start with the highest priority task."}
                  ]
                }
                """;
    }
}

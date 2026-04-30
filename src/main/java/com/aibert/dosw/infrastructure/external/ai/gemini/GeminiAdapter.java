package com.aibert.dosw.infrastructure.external.ai.gemini;

import com.aibert.dosw.domain.model.context.PlanningContext;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.AiProviderPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.RestTemplate;

/**
 * Adapter for the Google Gemini API.
 * Active when ai.provider=gemini in application.yml.
 *
 * Flow:
 * 1. Receives PlanningContext (precomputed by Java)
 * 2. Builds a compact prompt with the data
 * 3. Calls Gemini 2.5 Flash
 * 4. Returns JSON suggestions in Spanish
 * 5. On failure, uses Java-defined fallback messages
 */
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "gemini")
@Slf4j
public class GeminiAdapter implements AiProviderPort {

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/" +
            "{model}:generateContent?key={apiKey}";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.gemini.model:gemini-2.5-flash}")
    private String model;

    public GeminiAdapter(RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @CircuitBreaker(name = "aiProvider", fallbackMethod = "fallback")
    public String getSuggestion(PlanningContext context) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = buildRequestBody(context);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    GEMINI_URL,
                    HttpMethod.POST,
                    entity,
                    getResponseType(),
                    model,
                    apiKey);

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null) {
                return extractTextFromResponse(response.getBody());
            }

        } catch (Exception e) {
            log.warn("Gemini unavailable: {}", e.getMessage());
        }

        return fallback(context, null);
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Builds the request body for Gemini API.
     * Uses ObjectMapper later to validate the JSON response.
     */
    private Map<String, Object> buildRequestBody(PlanningContext context) {

        String prompt = buildPrompt(context);

        return Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.3,
                        "maxOutputTokens", 300,
                        "responseMimeType", "application/json"));
    }

    /**
     * Builds the prompt with data already computed by Java.
     * The prompt stays small because the heavy work is done in the domain.
     */
    private String buildPrompt(PlanningContext context) {

        PlanningTask topTask = context.getTopTask();

        String topTaskInfo = topTask != null
                ? String.format(
                        "- Materia: %s%n" +
                                "- Tarea: %s%n" +
                                "- Dias restantes: %d%n" +
                                "- Score de prioridad: %.0f/100",
                        topTask.getSubjectName(),
                        topTask.getTitle(),
                        topTask.getDaysRemaining(),
                        topTask.getPriorityScore())
                : "- Sin tareas urgentes hoy";

        return String.format("""
                Eres AI.BERT, asistente academico universitario.

                Datos CALCULADOS del estudiante:

                MATERIAS EN RIESGO:
                %s

                TAREA MAS URGENTE:
                %s

                HORAS DISPONIBLES HOY: %.1f horas

                PLAN CALCULADO PARA HOY:
                %s

                Responde SOLO con este JSON (sin texto extra):
                {
                  "mensaje_principal": "mensaje motivador maximo 2 oraciones",
                  "sugerencias": [
                  {
                    "tipo": "STUDY_TODAY|RESCHEDULE|ALERT|REST",
                    "texto": "sugerencia especifica en espanol colombiano"
                  }
                  ]
                }
                """,
                context.getRiskySummary(),
                topTaskInfo,
                context.getAvailableHoursToday(),
                context.getDailyPlanSummary());
    }

    /**
     * Extracts the response text from Gemini.
     * Expected structure:
     * { "candidates": [{ "content": { "parts": [{ "text": "..." }] } }] }
     */
    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<String, Object> body) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                log.warn("Gemini returned empty candidates");
                return buildJavaFallbackJson("Sin respuesta de la IA");
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            String text = (String) parts.get(0).get("text");

            objectMapper.readTree(text);
            return text;

        } catch (JsonProcessingException e) {
            log.warn("Gemini returned non-JSON text: {}", e.getMessage());
            return buildJavaFallbackJson("Respuesta de IA no estructurada");
        } catch (Exception e) {
            log.error("Error parsing Gemini response", e);
            return buildJavaFallbackJson("Error procesando respuesta");
        }
    }

    /**
     * Returns the type reference for a typed exchange response.
     */
    private ParameterizedTypeReference<Map<String, Object>> getResponseType() {
        return new ParameterizedTypeReference<>() {
        };
    }

    /**
     * Fallback triggered by the circuit breaker.
     */
    public String fallback(PlanningContext context, Exception ex) {
        log.info("Using Java fallback in GeminiAdapter. Cause: {}",
                ex != null ? ex.getMessage() : "timeout/unavailable");

        PlanningTask topTask = context.getTopTask();
        String topTaskName = topTask != null
                ? topTask.getTitle()
                : "tus tareas pendientes";

        String subjectName = topTask != null
                ? topTask.getSubjectName()
                : "tus materias";

        return buildJavaFallbackJson(topTaskName, subjectName, context);
    }

    /**
     * Builds a fallback JSON response using context data.
     */
    private String buildJavaFallbackJson(
            String taskName,
            String subjectName,
            PlanningContext context) {

        boolean hasRisk = !"No critical-risk subjects"
                .equals(context.getRiskySummary());

        String alertSuggestion = hasRisk
                ? String.format(
                        "{\"tipo\": \"ALERT\", " +
                                "\"texto\": \"Presta atencion a: %s\"}",
                        context.getRiskySummary())
                : "{\"tipo\": \"STUDY_TODAY\", " +
                        "\"texto\": \"Manten el ritmo de estudio\"}";

        return String.format("""
                {
                  "mensaje_principal": "Hoy enfocate en %s de %s. Cada hora cuenta!",
                  "sugerencias": [
                  {
                    "tipo": "STUDY_TODAY",
                    "texto": "Comienza con la tarea de mayor prioridad"
                  },
                  %s
                  ]
                }
                """, taskName, subjectName, alertSuggestion);
    }

    /**
     * Builds a simplified fallback JSON response without context.
     */
    private String buildJavaFallbackJson(String reason) {
        return String.format("""
                {
                  "mensaje_principal": "Revisa tu plan de estudio para hoy.",
                  "sugerencias": [
                  {
                    "tipo": "STUDY_TODAY",
                    "texto": "Comienza con las tareas mas urgentes"
                  }
                  ]
                }
                """);
    }
}

package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.context.PlanningContext;

/**
 * Outbound port for any AI provider.
 * The domain layer only knows this interface,
 * not whether it is Gemini, Groq, or Ollama.
 */
public interface AiProviderPort {

    /**
     * Sends the student context to the AI provider
     * and returns a JSON payload with generated suggestions.
     *
     * @param context full student context
     * @return JSON string with suggestions
     */
    String getSuggestion(PlanningContext context);

    /**
     * Checks whether the AI provider is available.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();
}

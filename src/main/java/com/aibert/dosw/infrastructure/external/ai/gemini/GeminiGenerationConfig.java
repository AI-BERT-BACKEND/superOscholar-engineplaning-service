package com.aibert.dosw.infrastructure.external.ai.gemini;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeminiGenerationConfig {
    private double temperature;
    private int maxOutputTokens;
    private String responseMimeType;
}

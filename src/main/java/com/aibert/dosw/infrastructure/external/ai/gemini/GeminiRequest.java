package com.aibert.dosw.infrastructure.external.ai.gemini;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeminiRequest {
    private List<GeminiContent> contents;
    private GeminiGenerationConfig generationConfig;
}

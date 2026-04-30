package com.aibert.dosw.infrastructure.external.ai.gemini;

import java.util.List;
import lombok.Data;

@Data
public class GeminiResponse {
    private List<GeminiCandidate> candidates;

    @Data
    public static class GeminiCandidate {
        private GeminiContent content;
    }
}

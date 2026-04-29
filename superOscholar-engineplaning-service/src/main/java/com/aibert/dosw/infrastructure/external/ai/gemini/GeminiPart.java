package com.aibert.dosw.infrastructure.external.ai.gemini;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeminiPart {
    private String text;
}

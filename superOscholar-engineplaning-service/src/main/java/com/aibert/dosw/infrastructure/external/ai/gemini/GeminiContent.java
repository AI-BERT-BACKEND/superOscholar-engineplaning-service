package com.aibert.dosw.infrastructure.external.ai.gemini;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeminiContent {
    private List<GeminiPart> parts;
}

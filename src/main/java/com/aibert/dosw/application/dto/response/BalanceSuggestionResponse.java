package com.aibert.dosw.application.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing a balance suggestion.
 */
@Getter
@Builder
public class BalanceSuggestionResponse {
    private final PrioritizedTaskResponse task;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String reason;
    private final String suggestionMessage;
}

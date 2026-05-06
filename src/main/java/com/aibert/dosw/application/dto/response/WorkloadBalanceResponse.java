package com.aibert.dosw.application.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing workload balance suggestions.
 */
@Getter
@Builder
public class WorkloadBalanceResponse {
    private final String studentId;
    private final List<BalanceSuggestionResponse> suggestions;
}

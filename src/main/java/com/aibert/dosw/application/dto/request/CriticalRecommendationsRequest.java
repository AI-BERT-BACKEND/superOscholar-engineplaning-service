package com.aibert.dosw.application.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriticalRecommendationsRequest {
    private List<CriticalTaskCandidateRequest> orderedTasks;
}

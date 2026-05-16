package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Top-level response for AIB-22.4: automatic estimation adjustment.
 */
@Getter
@Builder
@Schema(description = "Result of the automatic estimation adjustment for a student's pending tasks")
public class AdjustEstimationsResponse {

    @Schema(description = "Computed correction factor for this task type (clamped to [0.5, 2.0]). Values > 1.0 indicate the student consistently takes longer than estimated.", example = "1.3")
    private final double adjustmentFactor;

    @Schema(description = "Pending TODO tasks of the same type with updated duration estimates")
    private final List<UpdatedEstimateResponse> updatedEstimates;

    @Schema(description = "Human-readable summary message", example = "Hemos ajustado las estimaciones de 3 tarea(s) de tipo TAREA.")
    private final String message;
}

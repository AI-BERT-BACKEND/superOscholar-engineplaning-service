package com.aibert.dosw.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * AIB-27: DTO for a day where overload was detected in the proposed plan
 * before the protection cap (MAX_MINUTES_PER_DAY = 240) was applied.
 */
@Getter
@Builder
@Schema(description = "Day where the proposed plan exceeded the daily study cap before protection was applied")
public class OverloadedDayResponse {

    @Schema(description = "Date of the overloaded day", example = "2026-05-20")
    private final LocalDate date;

    @Schema(description = "Minutes exceeding MAX_MINUTES_PER_DAY (= proposed total – 240)", example = "45")
    private final int excessMinutes;
}

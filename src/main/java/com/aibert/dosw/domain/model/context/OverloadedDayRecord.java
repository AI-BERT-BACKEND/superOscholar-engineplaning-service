package com.aibert.dosw.domain.model.context;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * AIB-27: Represents a day where the proposed plan would have exceeded
 * MAX_MINUTES_PER_DAY = 240 before the overload-protection cap was applied.
 */
@Getter
@Builder
public class OverloadedDayRecord {

    /** The date that would have been overloaded. */
    private final LocalDate date;

    /**
     * Minutes over the limit (= proposed daily total − MAX_MINUTES_PER_DAY).
     * Always &gt; 0.
     */
    private final int excessMinutes;
}

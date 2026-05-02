package com.aibert.dosw.domain.model.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a time block that is explicitly marked as "not available" by the student.
 * E.g., personal events, doctor appointments, or rest periods.
 */
@Getter
@Builder
public class UnavailableBlock {
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String reason;
}

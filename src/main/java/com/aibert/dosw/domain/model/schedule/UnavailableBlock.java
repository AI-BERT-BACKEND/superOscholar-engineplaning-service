package com.aibert.dosw.domain.model.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a specific block of time marked as unavailable by the student.
 * (e.g., Doctor appointment, lunch break, class).
 */
@Getter
@Builder
@Jacksonized
public class UnavailableBlock {
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String reason;
}

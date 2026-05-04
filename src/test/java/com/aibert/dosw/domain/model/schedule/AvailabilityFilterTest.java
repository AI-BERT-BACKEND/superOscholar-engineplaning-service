package com.aibert.dosw.domain.model.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvailabilityFilterTest {

    @Test
    void shouldSplitTimeSlotWhenOverlappingWithUnavailableBlock() {
        TimeSlot original = TimeSlot.builder()
            .startTime(LocalTime.of(14, 0))
            .endTime(LocalTime.of(18, 0))
            .build();

        UnavailableBlock block = UnavailableBlock.builder()
            .date(LocalDate.now())
            .startTime(LocalTime.of(15, 0))
            .endTime(LocalTime.of(16, 0))
            .build();

        List<TimeSlot> result = AvailabilityFilter.removeUnavailableBlocks(List.of(original), List.of(block));

        assertEquals(2, result.size());
        assertEquals(LocalTime.of(14, 0), result.get(0).getStartTime());
        assertEquals(LocalTime.of(15, 0), result.get(0).getEndTime());
        
        assertEquals(LocalTime.of(16, 0), result.get(1).getStartTime());
        assertEquals(LocalTime.of(18, 0), result.get(1).getEndTime());
    }
}

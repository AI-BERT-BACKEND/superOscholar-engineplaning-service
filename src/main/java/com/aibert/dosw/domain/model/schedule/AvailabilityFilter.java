package com.aibert.dosw.domain.model.schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * Domain service responsible for filtering out unavailable time blocks
 * from the student's base daily schedule.
 */
@UtilityClass
public class AvailabilityFilter {

    /**
     * Subtracts a list of unavailable blocks from the available time slots.
     * Splits available slots if an unavailable block falls in the middle of it.
     *
     * @param availableSlots The baseline available slots
     * @param blockedBlocks  The explicitly marked unavailable blocks
     * @return A new list of clean, genuinely available time slots
     */
    public static List<TimeSlot> removeUnavailableBlocks(
            List<TimeSlot> availableSlots, 
            List<UnavailableBlock> blockedBlocks) {
            
        if (availableSlots == null || availableSlots.isEmpty()) {
            return List.of();
        }
        if (blockedBlocks == null || blockedBlocks.isEmpty()) {
            return new ArrayList<>(availableSlots);
        }

        List<TimeSlot> currentSlots = new ArrayList<>(availableSlots);

        for (UnavailableBlock blocked : blockedBlocks) {
            List<TimeSlot> nextSlots = new ArrayList<>();
            
            for (TimeSlot slot : currentSlots) {
                // If there is no overlap at all
                if (blocked.getEndTime().compareTo(slot.getStartTime()) <= 0 ||
                    blocked.getStartTime().compareTo(slot.getEndTime()) >= 0) {
                    nextSlots.add(slot);
                    continue;
                }

                // If there is an overlap, split the available slot
                // 1. Keep the left part of the slot (before the block)
                if (slot.getStartTime().isBefore(blocked.getStartTime())) {
                    nextSlots.add(TimeSlot.builder()
                            .startTime(slot.getStartTime())
                            .endTime(blocked.getStartTime())
                            .build());
                }
                
                // 2. Keep the right part of the slot (after the block)
                if (slot.getEndTime().isAfter(blocked.getEndTime())) {
                    nextSlots.add(TimeSlot.builder()
                            .startTime(blocked.getEndTime())
                            .endTime(slot.getEndTime())
                            .build());
                }
            }
            currentSlots = nextSlots; // Continue filtering with the newly split slots
        }

        // Sort chronologically
        currentSlots.sort(Comparator.comparing(TimeSlot::getStartTime));
        return currentSlots;
    }
}

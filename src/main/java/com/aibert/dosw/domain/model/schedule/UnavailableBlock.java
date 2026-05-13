package com.aibert.dosw.domain.model.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a specific block of time marked as unavailable by the student.
 * (e.g., Doctor appointment, lunch break, class).
 *
 * <p>Per R16 RN-04 and R17 RN-03: blocks of type PERSONAL, DESCANSO or SOCIAL
 * are fully protected and cannot receive academic tasks under any circumstance.</p>
 */
@Getter
@Builder
@Jacksonized
public class UnavailableBlock {
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String reason;

    /**
     * Semantic classification of this block.
     * Defaults to {@link BlockType#OTRO} when not explicitly set.
     */
    @Builder.Default
    private final BlockType blockType = BlockType.OTRO;

    /**
     * Returns true if this block is protected and cannot receive academic tasks.
     * Per R16 RN-04: PERSONAL, DESCANSO and SOCIAL blocks are always protected.
     *
     * @return true if the block type is PERSONAL, DESCANSO, or SOCIAL
     */
    public boolean isProtected() {
        return blockType == BlockType.PERSONAL
                || blockType == BlockType.DESCANSO
                || blockType == BlockType.SOCIAL;
    }
}


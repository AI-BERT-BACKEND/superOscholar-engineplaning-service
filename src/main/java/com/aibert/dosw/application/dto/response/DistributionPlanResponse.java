package com.aibert.dosw.application.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing the final weekly distribution plan.
 * It explicitly captures the "unassignedTasks" handling requirement.
 */
@Getter
@Builder
public class DistributionPlanResponse {
    private final String studentId;
    
    /** 
     * Tasks that were successfully assigned to available time blocks.
     */
    private final List<ScheduledBlockResponse> assignedBlocks;
    
    /** 
     * Tasks that could not be assigned because the available time 
     * was insufficient to cover them before their deadlines.
     */
    private final List<PrioritizedTaskResponse> unassignedTasks;
    
    private final boolean fullyAssigned;
}

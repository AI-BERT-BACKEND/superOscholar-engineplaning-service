package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;

/**
 * Input port for the Task Distribution Engine (R16).
 * Defines the contract to automatically distribute tasks into available time slots.
 */
public interface DistributeTasksUseCase {

    /**
     * Generates an optimal weekly distribution plan by assigning pending tasks 
     * into the student's free time blocks, strictly prioritizing HIGH/CRITICAL tasks.
     *
     * @param studentId The ID of the student
     * @return The calculated weekly distribution plan
     */
    WeeklyDistributionPlan distribute(String studentId);
}

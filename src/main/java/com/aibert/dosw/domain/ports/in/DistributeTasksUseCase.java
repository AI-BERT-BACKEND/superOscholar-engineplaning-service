package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import java.time.LocalDate;

/**
 * Input port for the Task Distribution Engine (AIB-24 / R16).
 * Defines the contract to automatically distribute tasks into available time
 * slots.
 */
public interface DistributeTasksUseCase {

    /**
     * Generates an optimal weekly distribution plan by assigning pending tasks
     * into the student's free time blocks, strictly prioritizing HIGH/CRITICAL
     * tasks.
     * Enforces MAX_MINUTES_PER_DAY = 240 and applies the AIB-22.4 correction
     * factor.
     *
     * @param studentId     The ID of the student
     * @param weekStartDate The Monday of the week to distribute (ISO 8601)
     * @return The calculated weekly distribution plan
     */
    WeeklyDistributionPlan distribute(String studentId, LocalDate weekStartDate);

    /**
     * Convenience overload — defaults weekStartDate to the current Monday.
     */
    default WeeklyDistributionPlan distribute(String studentId) {
        return distribute(studentId, LocalDate.now().with(java.time.DayOfWeek.MONDAY));
    }
}

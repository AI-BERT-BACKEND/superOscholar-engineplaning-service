package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import java.time.LocalDate;

/**
 * Input port for the Dynamic Rebalancing Engine (R17).
 * Handles student failures and recalibrates the entire week.
 */
public interface RebalanceTasksUseCase {

    /**
     * Reports a failure to complete a task and immediately triggers a 
     * schedule re-distribution to accommodate the missed hours.
     *
     * @param studentId   The ID of the student
     * @param taskId      The ID of the failed task
     * @param failedDate  The date the failure occurred
     * @param hoursMissed The amount of hours that were not completed
     * @param reason      Optional reason for the failure
     * @return The newly rebalanced weekly distribution plan
     */
    WeeklyDistributionPlan reportFailureAndRebalance(
            String studentId,
            String taskId,
            LocalDate failedDate,
            double hoursMissed,
            String reason);

    /**
     * Forces a complete reorganization of all pending tasks into the 
     * remaining free time of the week, producing a new updated plan.
     *
     * @param studentId The ID of the student
     * @return The newly reorganized weekly distribution plan
     */
    WeeklyDistributionPlan reorganizePlan(String studentId);
}

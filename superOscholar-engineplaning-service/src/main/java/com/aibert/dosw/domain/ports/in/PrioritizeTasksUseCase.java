package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.util.List;

/**
 * Input port for use case R14.
 * Defines the contract for prioritizing academic tasks.
 *
 * The priority is calculated with:
 * score = (gradeFactor * 0.35) + (proximityFactor * 0.35)
 * + (weightFactor * 0.20) + (creditsFactor * 0.10)
 */
public interface PrioritizeTasksUseCase {

    /**
     * Prioritizes all pending tasks for a student.
     * Calculates the score for each task and orders them
     * from higher to lower priority.
     *
     * @param userId           student id
     * @param totalCredits     total credits for the term
     * @param forceRecalculate if true recalculates even if a score exists
     * @return tasks ordered by descending priority
     */
    List<PlanningTask> prioritize(
            String userId,
            int totalCredits,
            boolean forceRecalculate);
}

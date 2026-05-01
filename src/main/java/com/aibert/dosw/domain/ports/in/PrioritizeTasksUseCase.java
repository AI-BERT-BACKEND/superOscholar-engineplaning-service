package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.util.List;

/**
 * Input port for the Prioritization Engine (R14).
 * Defines the contract for prioritizing academic tasks based on mathematical logic.
 */
public interface PrioritizeTasksUseCase {

    /**
     * Prioritizes all pending tasks for a student.
     * Calculates the priority score for each task and orders them
     * from highest to lowest priority.
     *
     * @param studentId        The ID of the student
     * @param forceRecalculate If true, recalculates even if a score already exists
     * @return List of tasks ordered by descending priority
     */
    List<PlanningTask> prioritize(String studentId, boolean forceRecalculate);
}

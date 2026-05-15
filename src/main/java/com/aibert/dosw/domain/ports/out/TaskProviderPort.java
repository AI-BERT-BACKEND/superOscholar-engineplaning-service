package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.util.List;

/**
 * Output port to retrieve and update tasks from the external task-service.
 * This abstracts the Feign Client communication in the domain layer.
 */
public interface TaskProviderPort {

    /**
     * Retrieves all pending tasks for a specific user from the task-service.
     *
     * @param studentId The ID of the student
     * @return List of pending planning tasks
     */
    List<PlanningTask> getPendingTasksByUser(String studentId);

    /**
     * Retrieves all tasks that have been scheduled for a specific user.
     *
     * @param studentId The ID of the student
     * @return List of scheduled tasks
     */
    List<PlanningTask> getScheduledTasksByUser(String studentId);

    /**
     * Sends the prioritized tasks back to the task-service to update their state.
     *
     * @param tasks The prioritized tasks to update
     */
    void updateTaskPriorities(List<PlanningTask> tasks);

    /**
     * Reports a failed study block to the task-service so it can
     * restore the pending hours or mark the task as incomplete.
     *
     * @param studentId   The ID of the student
     * @param taskId      The ID of the task
     * @param hoursMissed The hours not completed
     * @param reason      The reason for the failure
     */
    void reportTaskFailure(String studentId, String taskId, double hoursMissed, String reason);
}

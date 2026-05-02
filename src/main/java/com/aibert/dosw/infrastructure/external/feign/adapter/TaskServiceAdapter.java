package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.infrastructure.external.feign.client.TaskServiceClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Secondary adapter that implements the TaskProviderPort using Feign.
 */
@Component
@RequiredArgsConstructor
public class TaskServiceAdapter implements TaskProviderPort {

    private final TaskServiceClient taskServiceClient;

    @Override
    public List<PlanningTask> getPendingTasksByUser(String studentId) {
        return taskServiceClient.getPendingTasks(studentId);
    }

    @Override
    public List<PlanningTask> getScheduledTasksByUser(String studentId) {
        return taskServiceClient.getScheduledTasks(studentId);
    }

    @Override
    public void updateTaskPriorities(List<PlanningTask> tasks) {
        taskServiceClient.updateTaskPriorities(tasks);
    }

    @Override
    public void reportTaskFailure(String studentId, String taskId, double hoursMissed, String reason) {
        taskServiceClient.reportTaskFailure(studentId, taskId, hoursMissed, reason);
    }
}

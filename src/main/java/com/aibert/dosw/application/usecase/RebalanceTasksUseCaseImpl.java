package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.DistributeTasksUseCase;
import com.aibert.dosw.domain.ports.in.RebalanceTasksUseCase;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the Dynamic Rebalancing Engine (R17).
 */
@Service
@RequiredArgsConstructor
public class RebalanceTasksUseCaseImpl implements RebalanceTasksUseCase {

    private final TaskProviderPort taskProviderPort;
    private final DistributeTasksUseCase distributeTasksUseCase;

    @Override
    public WeeklyDistributionPlan reportFailureAndRebalance(
            String studentId, 
            String taskId, 
            LocalDate failedDate, 
            double hoursMissed, 
            String reason) {
            
        // 1. Notify the external task-service about the failure.
        // This should cause the task-service to increase the pending/estimated hours of the task.
        taskProviderPort.reportTaskFailure(studentId, taskId, hoursMissed, reason);

        // 2. Trigger a full dynamic re-distribution with the updated data
        WeeklyDistributionPlan newPlan = distributeTasksUseCase.distribute(studentId);
        
        // 3. Identify and mark critical alerts
        identifyAndMarkCriticalTasks(newPlan);
        
        return newPlan;
    }

    @Override
    public WeeklyDistributionPlan reorganizePlan(String studentId) {
        WeeklyDistributionPlan newPlan = distributeTasksUseCase.distribute(studentId);
        identifyAndMarkCriticalTasks(newPlan);
        return newPlan;
    }

    private void identifyAndMarkCriticalTasks(WeeklyDistributionPlan plan) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        if (plan.getAssignedBlocks() != null) {
            plan.getAssignedBlocks().forEach(block -> {
                PlanningTask task = block.getTask();
                if (task.getDueDate() != null && !task.getDueDate().isAfter(tomorrow)) {
                    task.markAsCriticalAlert();
                }
            });
        }
        
        if (plan.getUnassignedTasks() != null) {
            plan.getUnassignedTasks().forEach(task -> {
                if (task.getDueDate() != null && !task.getDueDate().isAfter(tomorrow)) {
                    task.markAsCriticalAlert();
                }
            });
        }
    }
}

package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.context.MovedTaskRecord;
import com.aibert.dosw.domain.model.schedule.ScheduledBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.DistributeTasksUseCase;
import com.aibert.dosw.domain.ports.in.RebalanceTasksUseCase;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the Dynamic Rebalancing Engine (R17).
 * Tracks moved tasks and enforces the max daily study hours limit (RN-04).
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
        taskProviderPort.reportTaskFailure(studentId, taskId, hoursMissed, reason);

        // 2. Capture the current plan snapshot (before rebalancing) to detect movements
        // We use the previous schedule as the "original" reference.
        Map<String, ScheduledBlock> originalBlockByTask = new HashMap<>();
        WeeklyDistributionPlan previousPlan = distributeTasksUseCase.distribute(studentId);
        if (previousPlan.getAssignedBlocks() != null) {
            for (ScheduledBlock block : previousPlan.getAssignedBlocks()) {
                originalBlockByTask.put(block.getTask().getId(), block);
            }
        }

        // 3. Trigger a full dynamic re-distribution with the updated data
        WeeklyDistributionPlan newPlan = distributeTasksUseCase.distribute(studentId);

        // 4. Identify and mark critical alerts (RN-02)
        identifyAndMarkCriticalTasks(newPlan);

        // 5. Compute movedTasks: tasks that changed date or time
        List<MovedTaskRecord> movedTasks = computeMovedTasks(newPlan, originalBlockByTask, failedDate);

        return WeeklyDistributionPlan.builder()
                .studentId(newPlan.getStudentId())
                .assignedBlocks(newPlan.getAssignedBlocks())
                .unassignedTasks(newPlan.getUnassignedTasks())
                .movedTasks(movedTasks)
                .build();
    }

    @Override
    public WeeklyDistributionPlan reorganizePlan(String studentId) {
        // Capture "before" snapshot
        Map<String, ScheduledBlock> originalBlockByTask = new HashMap<>();
        WeeklyDistributionPlan previousPlan = distributeTasksUseCase.distribute(studentId);
        if (previousPlan.getAssignedBlocks() != null) {
            for (ScheduledBlock block : previousPlan.getAssignedBlocks()) {
                originalBlockByTask.put(block.getTask().getId(), block);
            }
        }

        WeeklyDistributionPlan newPlan = distributeTasksUseCase.distribute(studentId);
        identifyAndMarkCriticalTasks(newPlan);

        List<MovedTaskRecord> movedTasks = computeMovedTasks(newPlan, originalBlockByTask, null);

        return WeeklyDistributionPlan.builder()
                .studentId(newPlan.getStudentId())
                .assignedBlocks(newPlan.getAssignedBlocks())
                .unassignedTasks(newPlan.getUnassignedTasks())
                .movedTasks(movedTasks)
                .build();
    }

    /**
     * Identifies tasks with imminent deadlines and marks them as CRITICAL (RN-02).
     */
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

    /**
     * Computes which tasks were relocated by comparing the new plan against the original block map.
     * Per R17 spec: movedTasks includes taskId, original block, and new block.
     *
     * @param newPlan              The new distribution plan
     * @param originalBlockByTask  Map of taskId → original ScheduledBlock before rebalancing
     * @param failedDate           The date that triggered the rebalance (may be null for manual reorganize)
     * @return List of moved task records
     */
    private List<MovedTaskRecord> computeMovedTasks(
            WeeklyDistributionPlan newPlan,
            Map<String, ScheduledBlock> originalBlockByTask,
            LocalDate failedDate) {

        if (newPlan.getAssignedBlocks() == null || originalBlockByTask.isEmpty()) {
            return List.of();
        }

        List<MovedTaskRecord> moved = new ArrayList<>();

        for (ScheduledBlock newBlock : newPlan.getAssignedBlocks()) {
            String taskId = newBlock.getTask().getId();
            ScheduledBlock originalBlock = originalBlockByTask.get(taskId);

            if (originalBlock == null) continue;

            boolean dateChanged = !originalBlock.getDate().equals(newBlock.getDate());
            boolean timeChanged = !originalBlock.getStartTime().equals(newBlock.getStartTime());

            if (dateChanged || timeChanged) {
                moved.add(MovedTaskRecord.builder()
                        .taskId(taskId)
                        .taskTitle(newBlock.getTask().getTitle())
                        .originalDate(originalBlock.getDate())
                        .originalStartTime(originalBlock.getStartTime())
                        .newDate(newBlock.getDate())
                        .newStartTime(newBlock.getStartTime())
                        .reason(failedDate != null
                                ? String.format("Rebalanceo por tarea no completada el %s", failedDate)
                                : "Reorganización manual del plan semanal")
                        .build());
            }
        }

        return moved;
    }
}


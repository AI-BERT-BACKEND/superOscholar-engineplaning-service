package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.domain.valueobjects.PriorityScore;
import com.aibert.dosw.infrastructure.config.PriorityWeightsProperties;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the prioritization use case (R14).
 * It fetches pending tasks, applies the mathematical prioritization algorithm,
 * sorts the tasks, and updates them via the output port.
 */
@Service
@RequiredArgsConstructor
public class PrioritizeTasksUseCaseImpl implements PrioritizeTasksUseCase {

    private final TaskProviderPort taskProviderPort;
    private final PriorityWeightsProperties weightsConfig;

    @Override
    public List<PlanningTask> prioritize(String studentId, boolean forceRecalculate) {

        // 1. Fetch pending tasks from the task-service (via Feign output port)
        List<PlanningTask> pendingTasks = taskProviderPort.getPendingTasksByUser(studentId);

        if (pendingTasks == null || pendingTasks.isEmpty()) {
            return List.of();
        }

        // 2. Apply mathematical algorithm to calculate PriorityScore for each task
        //    using configurable weights from application.yml
        for (PlanningTask task : pendingTasks) {

            // Only recalculate if forced or if the task has no score
            if (forceRecalculate || task.getPriorityLevel() == null) {

                double weight = task.getTaskWeightInGrade() != null ? task.getTaskWeightInGrade() : 0.0;

                PriorityScore score = PriorityScore.calculate(
                        task.getDueDate(),
                        weight,
                        task.getEstimatedHours(),
                        weightsConfig.getWeightProximity(),
                        weightsConfig.getWeightAcademic(),
                        weightsConfig.getWeightTime());

                task.assignPriority(score);
            }
        }

        // 3. Sort the tasks based on the calculated priority score (Descending order)
        List<PlanningTask> prioritizedTasks = pendingTasks.stream()
                .sorted(Comparator.comparingDouble(PlanningTask::getPriorityScore).reversed())
                .toList();

        // 4. Send the updated priorities back to the task-service
        taskProviderPort.updateTaskPriorities(prioritizedTasks);

        return prioritizedTasks;
    }
}

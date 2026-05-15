package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.ports.out.AcademicWeightProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import com.aibert.dosw.domain.valueobjects.PriorityScore;
import com.aibert.dosw.infrastructure.config.PriorityWeightsProperties;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the prioritization use case (R14).
 * It fetches pending tasks, applies weighted prioritization rules,
 * sorts the tasks, and updates them via the output port.
 */
@Service
@RequiredArgsConstructor
public class PrioritizeTasksUseCaseImpl implements PrioritizeTasksUseCase {

    private final TaskProviderPort taskProviderPort;
    private final AcademicWeightProviderPort academicWeightProviderPort;
    private final PriorityWeightsProperties weightsConfig;

    @Override
    public List<PlanningTask> prioritize(String studentId, boolean forceRecalculate) {

        // 1. Fetch pending tasks from the task-service (via Feign output port)
        List<PlanningTask> pendingTasks = taskProviderPort.getPendingTasksByUser(studentId);

        if (pendingTasks == null || pendingTasks.isEmpty()) {
            return List.of();
        }

        // 2. Filter active tasks and apply weighted priority
        List<PlanningTask> activeTasks = pendingTasks.stream()
                .filter(this::isActive)
                .collect(Collectors.toList());

        for (PlanningTask task : activeTasks) {
            if (forceRecalculate || task.getPriorityLevel() == null) {
                double academicWeight = academicWeightProviderPort
                        .getAcademicWeight(studentId, task.getSubjectName())
                        .map(AcademicWeight::getValue)
                        .orElse(0.0);

                PriorityScore score = PriorityScore.calculate(
                        task.getDueDate(),
                        academicWeight,
                        task.getEstimatedHours(),
                        weightsConfig.getWeightProximity(),
                        weightsConfig.getWeightAcademic(),
                        weightsConfig.getWeightTime());

                task.assignPriority(score);
            }
        }

        // 3. Sort the tasks by priority score (desc) and deadline (asc)
        List<PlanningTask> prioritizedTasks = activeTasks.stream()
                .sorted(Comparator.comparingDouble(PlanningTask::getPriorityScore).reversed()
                        .thenComparing(this::dueDateOrMax))
                .toList();

        // 4. Send the updated priorities back to the task-service
        taskProviderPort.updateTaskPriorities(prioritizedTasks);

        return prioritizedTasks;
    }

    private boolean isActive(PlanningTask task) {
        return task != null
                && (task.getStatus() == TaskStatus.PENDING
                        || task.getStatus() == TaskStatus.IN_PROGRESS);
    }

    private LocalDate dueDateOrMax(PlanningTask task) {
        return Optional.ofNullable(task)
                .map(PlanningTask::getDueDate)
                .orElse(LocalDate.MAX);
    }
}

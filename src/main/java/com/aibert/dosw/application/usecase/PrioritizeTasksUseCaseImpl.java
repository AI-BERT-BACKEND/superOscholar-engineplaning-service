package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.ports.out.AcademicWeightProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import com.aibert.dosw.domain.valueobjects.PriorityScore;
import com.aibert.dosw.infrastructure.config.PriorityWeightsProperties;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the prioritization use case (AIB-22).
 * It fetches pending tasks, applies weighted prioritization rules,
 * sorts the tasks, and updates them via the output port.
 */
@Service
@Slf4j
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
                        log.info("FA-01: Sin tareas activas para el estudiante '{}'. Se omite la priorización.",
                                        studentId);
                        return List.of();
                }

                // 2. Filter active tasks and apply weighted priority
                List<PlanningTask> activeTasks = pendingTasks.stream()
                                .filter(this::isActive)
                                .collect(Collectors.toList());

                for (PlanningTask task : activeTasks) {
                        double correctedEstimatedHours = applyTimeCorrection(task.getEstimatedHours());
                        task.applyDurationCorrection((int) Math.round(correctedEstimatedHours * 60));

                        if (forceRecalculate || task.getPriorityLevel() == null) {
                                double academicWeight = academicWeightProviderPort
                                                .getAcademicWeight(studentId, task.getSubjectId())
                                                .map(AcademicWeight::getValue)
                                                .orElse(0.0);
                                LocalDateTime deadline = resolveDeadline(task);

                                PriorityScore score = PriorityScore.calculate(
                                                deadline,
                                                academicWeight,
                                                correctedEstimatedHours,
                                                weightsConfig.getWeightProximity(),
                                                weightsConfig.getWeightAcademic(),
                                                weightsConfig.getWeightTime());

                                task.assignPriority(score);
                        }
                }

                // 3. Sort the tasks by priority score (desc) and deadline (asc)
                List<PlanningTask> prioritizedTasks = activeTasks.stream()
                                .sorted(Comparator.comparingDouble(PlanningTask::getPriorityScore).reversed()
                                                .thenComparing(this::deadlineOrMax))
                                .toList();

                // 4. Send the updated priorities back to the task-service
                taskProviderPort.updateTaskPriorities(prioritizedTasks);

                log.info("Se priorizaron {} tarea(s) correctamente para el estudiante '{}'", prioritizedTasks.size(),
                                studentId);

                return prioritizedTasks;
        }

        private boolean isActive(PlanningTask task) {
                return task != null
                                && (task.getStatus() == TaskStatus.TODO
                                                || task.getStatus() == TaskStatus.IN_PROGRESS);
        }

        private LocalDateTime deadlineOrMax(PlanningTask task) {
                return Optional.ofNullable(resolveDeadline(task))
                                .orElse(LocalDateTime.MAX);
        }

        private LocalDateTime resolveDeadline(PlanningTask task) {
                if (task == null) {
                        return null;
                }
                if (task.getDueDateTime() != null) {
                        return task.getDueDateTime();
                }
                if (task.getDueDate() != null) {
                        return task.getDueDate().atTime(23, 59);
                }
                return null;
        }

        private double applyTimeCorrection(double estimatedHours) {
                double safeFactor = weightsConfig.getTimeCorrectionFactor();
                if (safeFactor <= 0) {
                        safeFactor = 1.0;
                }
                return Math.max(estimatedHours, 0.0) * safeFactor;
        }
}

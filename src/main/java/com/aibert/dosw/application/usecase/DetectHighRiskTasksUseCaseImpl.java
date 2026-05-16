package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.risk.HighRiskTaskResult;
import com.aibert.dosw.domain.model.risk.RiskLevel;
import com.aibert.dosw.domain.model.risk.RiskSummary;
import com.aibert.dosw.domain.model.risk.RiskTaskDetail;
import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.ports.in.DetectHighRiskTasksUseCase;
import com.aibert.dosw.domain.ports.out.AcademicWeightProviderPort;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implements high-risk task detection (AIB-22.3).
 *
 * <p>
 * Crosses each task's {@code estimatedDurationMinutes} with the student's
 * available minutes until the deadline. Assigns risk levels:
 * <ul>
 * <li>{@code HIGH} — available &lt; 70% of estimated</li>
 * <li>{@code MEDIUM} — available between 70% and 85% of estimated</li>
 * </ul>
 * Tasks with {@code academicWeight > 0.30} are prioritized in the report
 * (RN-02).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DetectHighRiskTasksUseCaseImpl implements DetectHighRiskTasksUseCase {

        private static final double HIGH_RISK_THRESHOLD = 0.70;
        private static final double MEDIUM_RISK_THRESHOLD = 0.85;

        private final TaskProviderPort taskProviderPort;
        private final ScheduleProviderPort scheduleProviderPort;
        private final AcademicWeightProviderPort academicWeightProviderPort;

        @Override
        public HighRiskTaskResult detectHighRiskTasks(String studentId) {

                // 1. Fetch active tasks
                List<PlanningTask> allTasks = taskProviderPort.getPendingTasksByUser(studentId);
                List<PlanningTask> activeTasks = allTasks != null
                                ? allTasks.stream()
                                                .filter(t -> t.getStatus() == TaskStatus.TODO
                                                                || t.getStatus() == TaskStatus.IN_PROGRESS)
                                                .toList()
                                : List.of();

                if (activeTasks.isEmpty()) {
                        log.info("AIB-22.3: Sin tareas activas para el estudiante '{}'. Se omite la detección de riesgos.",
                                        studentId);
                        return buildEmptyResult("No hay tareas activas para analizar.");
                }

                // 2. Fetch availability as Map<LocalDate, Integer> (minutes per day)
                List<DailySchedule> schedules = scheduleProviderPort.getWeeklySchedule(studentId);
                if (schedules == null || schedules.isEmpty()) {
                        log.info("AIB-22.3 FA-01: Sin disponibilidad configurada para el estudiante '{}'.", studentId);
                        return buildEmptyResult("Configura tu disponibilidad para activar la detección de riesgos.");
                }

                Map<LocalDate, Integer> availabilityMap = schedules.stream()
                                .collect(Collectors.toMap(
                                                DailySchedule::getDate,
                                                s -> (int) Math.round(s.getTotalAvailableHours() * 60),
                                                Integer::sum));

                // 3. Evaluate each task
                List<RiskTaskDetail> riskyTasks = activeTasks.stream()
                                .map(task -> evaluateTask(task, availabilityMap, studentId))
                                .filter(detail -> detail.getRiskLevel() != RiskLevel.NONE)
                                .sorted(Comparator
                                                .comparingDouble(RiskTaskDetail::getAcademicWeight).reversed()
                                                .thenComparing(d -> d.getRiskLevel() == RiskLevel.HIGH ? 0 : 1))
                                .toList();

                // 4. Build summary
                int totalAtRisk = riskyTasks.size();
                double affectedLoadPercent = activeTasks.isEmpty() ? 0.0
                                : Math.round((double) totalAtRisk / activeTasks.size() * 10000.0) / 100.0;

                RiskSummary summary = RiskSummary.builder()
                                .totalAtRisk(totalAtRisk)
                                .affectedLoadPercent(affectedLoadPercent)
                                .build();

                String message = totalAtRisk == 0
                                ? "No se detectaron tareas en riesgo."
                                : String.format("Se detectaron %d tarea(s) en riesgo académico.", totalAtRisk);

                log.info("AIB-22.3: Se detectaron {} tarea(s) en riesgo para el estudiante '{}'", totalAtRisk,
                                studentId);

                return HighRiskTaskResult.builder()
                                .highRiskTasks(riskyTasks)
                                .riskSummary(summary)
                                .message(message)
                                .build();
        }

        private RiskTaskDetail evaluateTask(PlanningTask task, Map<LocalDate, Integer> availabilityMap,
                        String studentId) {
                int estimatedMinutes = (int) Math.round(task.getEstimatedHours() * 60);
                int availableMinutes = calculateAvailableMinutes(task, availabilityMap);

                double academicWeight = academicWeightProviderPort
                                .getAcademicWeight(studentId, task.getSubjectName())
                                .map(AcademicWeight::getValue)
                                .orElse(0.0);

                RiskLevel level = determineRiskLevel(availableMinutes, estimatedMinutes);

                return RiskTaskDetail.builder()
                                .taskId(task.getId())
                                .title(task.getTitle())
                                .riskLevel(level)
                                .availableMinutes(availableMinutes)
                                .estimatedDurationMinutes(estimatedMinutes)
                                .academicWeight(academicWeight)
                                .build();
        }

        private int calculateAvailableMinutes(PlanningTask task, Map<LocalDate, Integer> availabilityMap) {
                if (task.getDueDate() == null) {
                        return 0;
                }
                LocalDate today = LocalDate.now();
                LocalDate deadline = task.getDueDate();

                return availabilityMap.entrySet().stream()
                                .filter(entry -> !entry.getKey().isBefore(today) && entry.getKey().isBefore(deadline))
                                .mapToInt(Map.Entry::getValue)
                                .sum();
        }

        private RiskLevel determineRiskLevel(int availableMinutes, int estimatedMinutes) {
                if (estimatedMinutes <= 0)
                        return RiskLevel.NONE;
                double ratio = (double) availableMinutes / estimatedMinutes;
                if (ratio < HIGH_RISK_THRESHOLD)
                        return RiskLevel.HIGH;
                if (ratio < MEDIUM_RISK_THRESHOLD)
                        return RiskLevel.MEDIUM;
                return RiskLevel.NONE;
        }

        private HighRiskTaskResult buildEmptyResult(String message) {
                return HighRiskTaskResult.builder()
                                .highRiskTasks(List.of())
                                .riskSummary(RiskSummary.builder().totalAtRisk(0).affectedLoadPercent(0.0).build())
                                .message(message)
                                .build();
        }
}

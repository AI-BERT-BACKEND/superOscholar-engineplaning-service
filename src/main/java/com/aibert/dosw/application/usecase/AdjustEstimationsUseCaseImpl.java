package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.dto.request.AdjustEstimationsRequest;
import com.aibert.dosw.application.dto.response.AdjustEstimationsResponse;
import com.aibert.dosw.application.dto.response.UpdatedEstimateResponse;
import com.aibert.dosw.application.service.CorrectionFactorStore;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.ports.in.AdjustEstimationsUseCase;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implements automatic estimation adjustment (AIB-22.4).
 *
 * <p>
 * Business rules:
 * <ul>
 * <li>RN-01 — At least {@value #MIN_SAMPLES} completed tasks of the same type
 * are required.</li>
 * <li>RN-02 — Factor = average of (actualTime / estimatedDuration) for the last
 * 10 tasks.</li>
 * <li>RN-03 — Factor is clamped to [{@value #MIN_FACTOR},
 * {@value #MAX_FACTOR}].</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdjustEstimationsUseCaseImpl implements AdjustEstimationsUseCase {

        static final int MIN_SAMPLES = 5;
        static final double MIN_FACTOR = 0.5;
        static final double MAX_FACTOR = 2.0;

        private final TaskProviderPort taskProviderPort;
        private final CorrectionFactorStore correctionFactorStore;

        @Override
        public AdjustEstimationsResponse adjustEstimations(String studentId, AdjustEstimationsRequest request) {

                // FA-02: actualTime not provided (or zero) — use estimated value, do not update
                // factor
                if (request.getActualTime() == null || request.getActualTime() <= 0) {
                        log.info(
                                        "AIB-22.4 FA-02: Sin tiempo real para la tarea '{}' del estudiante '{}'. No se actualiza el factor.",
                                        request.getCompletedTaskId(), studentId);
                        return buildSkippedResponse(
                                        "No se registró tiempo real. El factor de ajuste no fue actualizado.");
                }

                // 1. Fetch the completed task from task-service to obtain its estimated
                // duration
                // (AIB-22.4 spec: estimatedDurationMinutes is NOT part of the request)
                Optional<PlanningTask> completedTaskOpt = taskProviderPort.getTaskById(request.getCompletedTaskId());
                if (completedTaskOpt.isEmpty() || completedTaskOpt.get().getEstimatedHours() <= 0) {
                        log.warn("AIB-22.4: No se pudo obtener la tarea '{}' desde task-service. No se registra el ratio.",
                                        request.getCompletedTaskId());
                        return buildSkippedResponse(
                                        "No se pudo obtener los datos de la tarea completada desde el servicio de tareas.");
                }
                double estimatedMinutes = completedTaskOpt.get().getEstimatedHours() * 60.0;
                double ratio = request.getActualTime() / estimatedMinutes;
                correctionFactorStore.record(studentId, request.getTaskType(), ratio);

                // 2. FA-01: Not enough history yet (RN-01)
                List<Double> ratios = correctionFactorStore.getRecentRatios(studentId, request.getTaskType());
                if (ratios.size() < MIN_SAMPLES) {
                        log.info("AIB-22.4 FA-01: El estudiante '{}' tiene {} muestra(s) del tipo '{}'. Se requieren mínimo {}.",
                                        studentId, ratios.size(), request.getTaskType(), MIN_SAMPLES);
                        return buildSkippedResponse(
                                        "Aún no hay suficientes datos para ajustar estimaciones. "
                                                        + "Se necesitan al menos " + MIN_SAMPLES
                                                        + " tareas completadas del mismo tipo.");
                }

                // 3. Calculate correction factor and clamp to [0.5, 2.0] (RN-02, RN-03)
                double rawFactor = ratios.stream().mapToDouble(Double::doubleValue).average().orElse(1.0);
                double adjustmentFactor = Math.max(MIN_FACTOR, Math.min(MAX_FACTOR, rawFactor));
                log.info("AIB-22.4: Factor de corrección para '{}'/{}: {} (promedio sin clamp: {})",
                                studentId, request.getTaskType(), adjustmentFactor, rawFactor);

                // 4. Fetch pending TODO tasks of the same type and apply correction
                List<PlanningTask> allPending = taskProviderPort.getPendingTasksByUser(studentId);
                List<PlanningTask> tasksToAdjust = allPending.stream()
                                .filter(t -> t.getStatus() == TaskStatus.TODO)
                                .filter(t -> request.getTaskType().equals(t.getType()))
                                .toList();

                List<UpdatedEstimateResponse> updatedEstimates = tasksToAdjust.stream()
                                .map(task -> applyCorrection(task, adjustmentFactor))
                                .toList();

                // 5. Push corrected durations back to task-service
                if (!tasksToAdjust.isEmpty()) {
                        taskProviderPort.updateTaskPriorities(tasksToAdjust);
                        log.info("AIB-22.4: {} tarea(s) actualizadas en task-service para el estudiante '{}'.",
                                        tasksToAdjust.size(), studentId);
                }

                String message = updatedEstimates.isEmpty()
                                ? "¡Factor de ajuste actualizado! No hay tareas pendientes del mismo tipo para ajustar."
                                : "Hemos ajustado las estimaciones de " + updatedEstimates.size()
                                                + " tarea(s) de tipo " + request.getTaskType().name() + ".";

                return AdjustEstimationsResponse.builder()
                                .adjustmentFactor(adjustmentFactor)
                                .updatedEstimates(updatedEstimates)
                                .message(message)
                                .build();
        }

        // -------------------------------------------------------------------------
        // Private helpers
        // -------------------------------------------------------------------------

        private UpdatedEstimateResponse applyCorrection(PlanningTask task, double factor) {
                int originalMinutes = (int) Math.round(task.getEstimatedHours() * 60);
                int adjustedMinutes = (int) Math.round(originalMinutes * factor);
                task.applyDurationCorrection(adjustedMinutes);

                return UpdatedEstimateResponse.builder()
                                .taskId(task.getId())
                                .title(task.getTitle())
                                .taskType(task.getType() != null ? task.getType().name() : null)
                                .originalEstimatedMinutes(originalMinutes)
                                .adjustedEstimatedMinutes(adjustedMinutes)
                                .build();
        }

        private AdjustEstimationsResponse buildSkippedResponse(String message) {
                return AdjustEstimationsResponse.builder()
                                .adjustmentFactor(1.0)
                                .updatedEstimates(List.of())
                                .message(message)
                                .build();
        }
}

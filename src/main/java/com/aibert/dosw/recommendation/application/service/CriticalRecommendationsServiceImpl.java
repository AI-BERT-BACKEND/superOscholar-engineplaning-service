package com.aibert.dosw.recommendation.application.service;

import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.recommendation.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.recommendation.domain.ports.in.CriticalRecommendationsUseCase;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AIB-22.2 — Recomendación de tareas críticas.
 * Filters prioritized tasks and returns up to 3 critical recommendations
 * (HIGH/CRITICAL priority, active status, deadline within 48 hours).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CriticalRecommendationsServiceImpl implements CriticalRecommendationsUseCase {

    private static final long CRITICAL_WINDOW_HOURS = 48L;
    private static final int MAX_RECOMMENDATIONS = 3;

    private final PrioritizeTasksUseCase prioritizeTasksUseCase;
    private final PlanningTaskMapper planningTaskMapper;

    @Override
    public CriticalRecommendationsResponse getRecommendations(
            String studentId,
            List<PrioritizedTaskResponse> orderedTasks,
            boolean forceRecalculate) {

        List<PrioritizedTaskResponse> tasks = resolveTaskList(studentId, orderedTasks, forceRecalculate);
        log.info("Evaluando recomendaciones críticas para el estudiante '{}' — total de tareas recibidas: {}",
                studentId, tasks.size());

        LocalDateTime cutoff = LocalDateTime.now().plusHours(CRITICAL_WINDOW_HOURS);

        List<PrioritizedTaskResponse> candidates = tasks.stream()
                .filter(task -> isActive(task.getStatus()))
                .filter(task -> isCriticalPriority(task.getPriorityLevel()))
                .filter(task -> isWithinWindow(task.getDeadline(), cutoff))
                .sorted(Comparator
                        .comparingInt(this::priorityRank).reversed()
                        .thenComparing(Comparator.comparing(
                                PrioritizedTaskResponse::getDeadline,
                                Comparator.nullsLast(Comparator.naturalOrder()))))
                .limit(MAX_RECOMMENDATIONS)
                .toList();

        int criticalCount = candidates.size();
        String message = criticalCount > 0
                ? "Tienes " + criticalCount + " tarea(s) crítica(s) que requieren atención inmediata"
                : "No tienes tareas críticas en este momento";

        log.info("Recomendaciones críticas generadas para '{}': {} resultado(s)", studentId, criticalCount);

        return CriticalRecommendationsResponse.builder()
                .criticalRecommendations(candidates)
                .criticalCount(criticalCount)
                .message(message)
                .build();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private List<PrioritizedTaskResponse> resolveTaskList(
            String studentId,
            List<PrioritizedTaskResponse> orderedTasks,
            boolean forceRecalculate) {

        if (orderedTasks != null && !orderedTasks.isEmpty()) {
            log.debug("Usando lista de tareas proporcionada por el llamador ({} tareas) para '{}'",
                    orderedTasks.size(), studentId);
            return orderedTasks;
        }

        log.debug("Lista de tareas no proporcionada — ejecutando priorización interna para '{}'", studentId);
        return prioritizeTasksUseCase.prioritize(studentId, forceRecalculate).stream()
                .map(planningTaskMapper::toPrioritizedResponse)
                .toList();
    }

    private boolean isActive(String status) {
        return "TODO".equalsIgnoreCase(status) || "IN_PROGRESS".equalsIgnoreCase(status);
    }

    private boolean isCriticalPriority(String priorityLevel) {
        return "HIGH".equalsIgnoreCase(priorityLevel) || "CRITICAL".equalsIgnoreCase(priorityLevel);
    }

    private boolean isWithinWindow(LocalDateTime deadline, LocalDateTime cutoff) {
        return deadline != null && deadline.isBefore(cutoff);
    }

    private int priorityRank(PrioritizedTaskResponse task) {
        if ("CRITICAL".equalsIgnoreCase(task.getPriorityLevel())) {
            return 2;
        }
        if ("HIGH".equalsIgnoreCase(task.getPriorityLevel())) {
            return 1;
        }
        return 0;
    }
}

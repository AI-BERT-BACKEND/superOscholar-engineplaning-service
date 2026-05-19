package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CriticalRecommendationsService {

    private static final long CRITICAL_WINDOW_HOURS = 48L;

    public CriticalRecommendationsResponse buildRecommendations(List<PrioritizedTaskResponse> orderedTasks) {
        List<PrioritizedTaskResponse> safeTasks = Optional.ofNullable(orderedTasks).orElse(List.of());

        List<PrioritizedTaskResponse> criticalTasks = safeTasks.stream()
                .filter(this::isActive)
                .filter(this::isCriticalPriority)
                .filter(this::isWithinWindow)
                .toList();

        int totalCritical = criticalTasks.size();

        List<PrioritizedTaskResponse> recommendations = criticalTasks.stream()
                .sorted(Comparator.comparingInt(this::priorityRank).reversed()
                        .thenComparing(this::deadlineOrMax))
                .limit(3)
                .toList();

        String message = totalCritical > 0
                ? "Tienes " + totalCritical + " tarea(s) crítica(s) que requieren atención inmediata"
                : "No tienes tareas críticas en este momento";

        return CriticalRecommendationsResponse.builder()
                .criticalRecommendations(recommendations)
                .criticalCount(totalCritical)
                .message(message)
                .build();
    }

    private boolean isActive(PrioritizedTaskResponse task) {
        String status = Optional.ofNullable(task.getStatus()).orElse("TODO");
        return status.equalsIgnoreCase("TODO") || status.equalsIgnoreCase("IN_PROGRESS");
    }

    private boolean isCriticalPriority(PrioritizedTaskResponse task) {
        String level = Optional.ofNullable(task.getPriorityLevel()).orElse("");
        return level.equalsIgnoreCase("CRITICAL") || level.equalsIgnoreCase("HIGH");
    }

    private boolean isWithinWindow(PrioritizedTaskResponse task) {
        LocalDateTime deadline = task.getDeadline();
        if (deadline == null || deadline.equals(LocalDateTime.MIN)) {
            return false;
        }
        long hoursLeft = ChronoUnit.HOURS.between(LocalDateTime.now(), deadline);
        return hoursLeft >= 0 && hoursLeft < CRITICAL_WINDOW_HOURS;
    }

    private int priorityRank(PrioritizedTaskResponse task) {
        String level = Optional.ofNullable(task.getPriorityLevel()).orElse("");
        if (level.equalsIgnoreCase("CRITICAL")) {
            return 2;
        }
        if (level.equalsIgnoreCase("HIGH")) {
            return 1;
        }
        return 0;
    }

    private LocalDateTime deadlineOrMax(PrioritizedTaskResponse task) {
        return Optional.ofNullable(task.getDeadline()).orElse(LocalDateTime.MAX);
    }
}

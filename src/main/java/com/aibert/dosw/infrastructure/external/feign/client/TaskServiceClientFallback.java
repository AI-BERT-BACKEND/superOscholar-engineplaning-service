package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponseDTO;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback para TaskServiceClient.
 * Se activa cuando task-service no está disponible o el circuit breaker está abierto.
 * Devuelve valores seguros para no interrumpir el flujo del planning-service.
 */
@Component
@Slf4j
public class TaskServiceClientFallback implements TaskServiceClient {

    @Override
    public List<TaskServiceResponseDTO> getPendingTasks(String studentId) {
        log.warn("task-service no disponible al obtener tareas pendientes del estudiante '{}'.", studentId);
        return Collections.emptyList();
    }

    @Override
    public List<TaskServiceResponseDTO> getScheduledTasks(String studentId) {
        log.warn("task-service no disponible al obtener tareas programadas del estudiante '{}'.", studentId);
        return Collections.emptyList();
    }

    @Override
    public void updateTaskPriorities(List<TaskServiceResponseDTO> tasks) {
        log.warn("task-service no disponible. No se pudieron actualizar {} prioridades.",
                tasks != null ? tasks.size() : 0);
    }

    @Override
    public void reportTaskFailure(String studentId, String taskId, double hoursMissed, String reason) {
        log.warn("task-service no disponible. No se reportó el fallo de taskId='{}' ({}h, razón: {}).",
                taskId, hoursMissed, reason);
    }
}

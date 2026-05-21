package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskServiceClientFallback implements TaskServiceClient {

    @Override
    public List<TaskServiceResponse> getTasksByStudent(String studentId) {
        log.warn("task-service no disponible al obtener tareas del estudiante '{}'.", sl(studentId));
        return Collections.emptyList();
    }

    @Override
    public void updateTaskPriorities(List<TaskServiceResponse> tasks) {
        log.warn("task-service no disponible. No se pudieron actualizar {} prioridades.", tasks.size());
    }

    @Override
    public void reportTaskFailure(String studentId, String taskId, double hoursMissed, String reason) {
        log.warn("task-service no disponible. No se reportó el fallo de taskId='{}' ({}h).",
                sl(taskId), hoursMissed);
    }

    @Override
    public TaskServiceResponse getTaskById(String taskId) {
        log.warn("task-service no disponible al obtener la tarea '{}'.", sl(taskId));
        return null;
    }

    private static String sl(String s) {
        return s == null ? "" : s.replaceAll("[\r\n]", "_");
    }
}

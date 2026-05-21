package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceStatusUpdateRequest;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceUpdateRequest;
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
    public void patchTask(String taskId, TaskServiceUpdateRequest request) {
        log.warn("task-service no disponible. No se pudo actualizar la tarea '{}'.", sl(taskId));
    }

    @Override
    public void patchTaskStatus(String taskId, TaskServiceStatusUpdateRequest request) {
        log.warn("task-service no disponible. No se pudo actualizar el estado de la tarea '{}'.", sl(taskId));
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

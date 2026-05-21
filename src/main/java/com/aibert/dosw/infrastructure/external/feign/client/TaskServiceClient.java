package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceStatusUpdateRequest;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceUpdateRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client para comunicarse con task-service.
 * El interceptor global (FeignClientInterceptor) propaga automáticamente
 * Authorization y X-User-Id, por lo que no se declaran como parámetros.
 */
@FeignClient(name = "task-service", url = "${feign.task-service.url}", fallback = TaskServiceClientFallback.class)
public interface TaskServiceClient {

    // Obtener todas las tareas del estudiante (TODO, IN_PROGRESS, SCHEDULED, etc.)
    // El filtro por estado se aplica localmente en TaskServiceAdapter.
    @GetMapping("/api/tasks/student/{studentId}")
    List<TaskServiceResponse> getTasksByStudent(@PathVariable("studentId") String studentId);

    // Actualizar campos de una tarea (priority, estimatedDurationMinutes, etc.)
    @PatchMapping("/api/tasks/{taskId}")
    void patchTask(@PathVariable("taskId") String taskId, @RequestBody TaskServiceUpdateRequest request);

    // Actualizar estado de tarea
    @PatchMapping("/api/tasks/{taskId}/status")
    void patchTaskStatus(@PathVariable("taskId") String taskId, @RequestBody TaskServiceStatusUpdateRequest request);

    // Obtener tarea por ID (incluye tareas completadas)
    @GetMapping("/api/tasks/{taskId}")
    TaskServiceResponse getTaskById(@PathVariable("taskId") String taskId);
}

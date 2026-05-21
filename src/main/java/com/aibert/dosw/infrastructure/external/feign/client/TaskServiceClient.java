package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    // Actualizar prioridades calculadas por el motor
    @PutMapping("/api/tasks/priorities")
    void updateTaskPriorities(@RequestBody List<TaskServiceResponse> tasks);

    // Notificar bloque de estudio fallido
    @PostMapping("/api/tasks/failure")
    void reportTaskFailure(
            @RequestParam("studentId") String studentId,
            @RequestParam("taskId") String taskId,
            @RequestParam("hoursMissed") double hoursMissed,
            @RequestParam("reason") String reason);

    // Obtener tarea por ID (incluye tareas completadas)
    @GetMapping("/api/tasks/{taskId}")
    TaskServiceResponse getTaskById(@PathVariable("taskId") String taskId);
}

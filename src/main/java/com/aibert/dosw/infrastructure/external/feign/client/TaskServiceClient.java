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
 * Retorna TaskServiceResponse (DTO) — la conversión a PlanningTask se hace en el adaptador.
 * El fallback se activa si task-service no responde o el circuit breaker está abierto.
 */
@FeignClient(
    name = "task-service",
    url = "${feign.task-service.url}",
    fallback = TaskServiceClientFallback.class
)
public interface TaskServiceClient {

    // R14 / R16 — Obtener tareas pendientes (TODO e IN_PROGRESS)
    @GetMapping("/api/tasks/student/{studentId}/pending")
    List<TaskServiceResponse> getPendingTasks(@PathVariable("studentId") String studentId);

    // R15 — Obtener tareas ya programadas (SCHEDULED)
    @GetMapping("/api/tasks/student/{studentId}/scheduled")
    List<TaskServiceResponse> getScheduledTasks(@PathVariable("studentId") String studentId);

    // R14 — Actualizar prioridades calculadas por el motor
    @PutMapping("/api/tasks/priorities")
    void updateTaskPriorities(@RequestBody List<TaskServiceResponse> tasks);

    // R17 — Notificar bloque de estudio fallido
    @PostMapping("/api/tasks/failure")
    void reportTaskFailure(
            @RequestParam("studentId") String studentId,
            @RequestParam("taskId") String taskId,
            @RequestParam("hoursMissed") double hoursMissed,
            @RequestParam("reason") String reason);
}

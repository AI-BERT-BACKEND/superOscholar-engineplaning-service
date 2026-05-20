package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import com.aibert.dosw.recommendation.application.dto.request.CriticalRecommendationsRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign Client para comunicarse con planning-service (puerto 1505).
 *
 * <p>
 * Expone los tres endpoints que el planning-service consume de este motor:
 * </p>
 * <ul>
 * <li>GET /planning/prioritization — lista priorizada de tareas</li>
 * <li>POST /planning/distribution — distribución semanal automática</li>
 * <li>POST /planning/recommendations/critical — hasta 3 recomendaciones
 * críticas</li>
 * </ul>
 *
 * <p>
 * El header {@code X-Student-Id} se envía explícitamente en cada llamada.
 * El JWT se propaga automáticamente a través de {@link
 * com.aibert.dosw.infrastructure.external.feign.config.FeignClientInterceptor}.
 * </p>
 */
@FeignClient(name = "planning-service", url = "${feign.planning-service.url}", fallback = PlanningServiceClientFallback.class)
public interface PlanningServiceClient {

    /**
     * Obtiene la lista de tareas priorizadas del estudiante.
     *
     * @param studentId        identificador del estudiante
     * @param forceRecalculate si {@code true} fuerza el recálculo de scores
     */
    @GetMapping("/planning/prioritization")
    ApiResponse<List<PrioritizedTaskResponse>> getPrioritizedTasks(
            @RequestHeader("X-Student-Id") String studentId,
            @RequestParam(name = "forceRecalculate", required = false) Boolean forceRecalculate);

    /**
     * Genera el plan de distribución semanal automático para el estudiante.
     *
     * @param studentId     identificador del estudiante
     * @param weekStartDate lunes de la semana a distribuir (ISO, opcional)
     */
    @PostMapping("/planning/distribution")
    ApiResponse<DistributionPlanResponse> generateDistribution(
            @RequestHeader("X-Student-Id") String studentId,
            @RequestParam(name = "weekStartDate", required = false) String weekStartDate);

    /**
     * Retorna hasta 3 recomendaciones críticas (prioridad HIGH/CRITICAL, deadline
     * dentro de 48 h).
     *
     * @param studentId identificador del estudiante
     * @param request   cuerpo opcional con tareas pre-ordenadas; si se omite el
     *                  motor prioriza automáticamente
     */
    @PostMapping("/planning/recommendations/critical")
    ApiResponse<CriticalRecommendationsResponse> getCriticalRecommendations(
            @RequestHeader("X-Student-Id") String studentId,
            @RequestBody(required = false) CriticalRecommendationsRequest request);
}

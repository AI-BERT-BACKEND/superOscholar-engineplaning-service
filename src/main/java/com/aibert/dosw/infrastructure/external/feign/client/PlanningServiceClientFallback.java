package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import com.aibert.dosw.recommendation.application.dto.request.CriticalRecommendationsRequest;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback para {@link PlanningServiceClient}.
 *
 * <p>
 * Se activa cuando planning-service no está disponible o el circuit breaker
 * está abierto. Devuelve respuestas vacías/seguras para no interrumpir el
 * flujo del motor de planificación.
 * </p>
 */
@Slf4j
@Component
public class PlanningServiceClientFallback implements PlanningServiceClient {

    @Override
    public ApiResponse<List<PrioritizedTaskResponse>> getPrioritizedTasks(
            String studentId, Boolean forceRecalculate) {
        log.warn(
                "[Fallback] planning-service no disponible al obtener tareas priorizadas del estudiante '{}'.",
                sl(studentId));
        return ApiResponse.success("planning-service no disponible — lista vacía", Collections.emptyList());
    }

    @Override
    public ApiResponse<DistributionPlanResponse> generateDistribution(
            String studentId, String weekStartDate) {
        log.warn(
                "[Fallback] planning-service no disponible al generar distribución para el estudiante '{}'.",
                sl(studentId));
        DistributionPlanResponse empty = DistributionPlanResponse.builder()
                .studentId(studentId)
                .assignedBlocks(Collections.emptyList())
                .unassignedTasks(Collections.emptyList())
                .movedTasks(Collections.emptyList())
                .criticalAlerts(Collections.emptyList())
                .overloadedDays(Collections.emptyList())
                .fullyAssigned(false)
                .message("planning-service no disponible — distribución vacía")
                .build();
        return ApiResponse.success(empty.getMessage(), empty);
    }

    @Override
    public ApiResponse<CriticalRecommendationsResponse> getCriticalRecommendations(
            String studentId, CriticalRecommendationsRequest request) {
        log.warn(
                "[Fallback] planning-service no disponible al obtener recomendaciones críticas del estudiante '{}'.",
                sl(studentId));
        CriticalRecommendationsResponse empty = CriticalRecommendationsResponse.builder()
                .criticalRecommendations(Collections.emptyList())
                .criticalCount(0)
                .message("planning-service no disponible — sin recomendaciones críticas")
                .build();
        return ApiResponse.success(empty.getMessage(), empty);
    }

    private static String sl(String s) {
        return s == null ? "" : s.replaceAll("[\r\n]", "_");
    }
}

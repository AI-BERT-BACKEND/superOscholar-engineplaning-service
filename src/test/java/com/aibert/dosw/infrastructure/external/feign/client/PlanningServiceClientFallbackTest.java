package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanningServiceClientFallbackTest {

    private final PlanningServiceClientFallback fallback = new PlanningServiceClientFallback();

    @Test
    void getPrioritizedTasks_returnsEmptyList() {
        ApiResponse<List<PrioritizedTaskResponse>> response = fallback.getPrioritizedTasks("student1", false);
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("planning-service no disponible"));
    }

    @Test
    void generateDistribution_returnsEmptyPlan() {
        ApiResponse<DistributionPlanResponse> response = fallback.generateDistribution("student1", null);
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("student1", response.getData().getStudentId());
        assertFalse(response.getData().isFullyAssigned());
        assertNotNull(response.getData().getAssignedBlocks());
        assertTrue(response.getData().getAssignedBlocks().isEmpty());
        assertNotNull(response.getData().getUnassignedTasks());
        assertTrue(response.getData().getUnassignedTasks().isEmpty());
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("planning-service no disponible"));
    }

    @Test
    void getCriticalRecommendations_returnsEmptyList() {
        ApiResponse<CriticalRecommendationsResponse> response = fallback.getCriticalRecommendations("student1", null);
        assertNotNull(response);
        assertNotNull(response.getData());
        assertNotNull(response.getData().getCriticalRecommendations());
        assertTrue(response.getData().getCriticalRecommendations().isEmpty());
        assertEquals(0, response.getData().getCriticalCount());
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("planning-service no disponible"));
    }
}

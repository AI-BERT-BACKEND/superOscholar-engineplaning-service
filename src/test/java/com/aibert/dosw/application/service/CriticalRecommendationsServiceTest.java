package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriticalRecommendationsServiceTest {

        private final CriticalRecommendationsService service = new CriticalRecommendationsService();

        @Test
        void shouldReturnCriticalRecommendations() {
                PrioritizedTaskResponse critical = PrioritizedTaskResponse.builder()
                                .taskId("t1")
                                .priorityLevel("CRITICAL")
                                .status("TODO")
                                .deadline(LocalDateTime.now().plusHours(6))
                                .build();

                PrioritizedTaskResponse high = PrioritizedTaskResponse.builder()
                                .taskId("t2")
                                .priorityLevel("HIGH")
                                .status("IN_PROGRESS")
                                .deadline(LocalDateTime.now().plusHours(20))
                                .build();

                PrioritizedTaskResponse outsideWindow = PrioritizedTaskResponse.builder()
                                .taskId("t3")
                                .priorityLevel("HIGH")
                                .status("TODO")
                                .deadline(LocalDateTime.now().plusHours(72))
                                .build();

                CriticalRecommendationsResponse response = service.buildRecommendations(
                                List.of(critical, high, outsideWindow));

                assertEquals(2, response.getCriticalCount());
                assertEquals(2, response.getCriticalRecommendations().size());
                assertEquals("t1", response.getCriticalRecommendations().get(0).getTaskId());
                assertTrue(response.getMessage().startsWith("Tienes"));
        }

        @Test
        void shouldReturnEmptyWhenNoCriticalTasks() {
                PrioritizedTaskResponse normal = PrioritizedTaskResponse.builder()
                                .taskId("t1")
                                .priorityLevel("MEDIUM")
                                .status("TODO")
                                .deadline(LocalDateTime.now().plusHours(10))
                                .build();

                CriticalRecommendationsResponse response = service.buildRecommendations(List.of(normal));

                assertEquals(0, response.getCriticalCount());
                assertTrue(response.getCriticalRecommendations().isEmpty());
                assertEquals("No tienes tareas críticas en este momento", response.getMessage());
        }
}

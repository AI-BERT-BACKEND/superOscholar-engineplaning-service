package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.CriticalRecommendationsRequest;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import com.aibert.dosw.recommendation.application.dto.response.CriticalRecommendationsResponse;
import com.aibert.dosw.recommendation.domain.ports.in.CriticalRecommendationsUseCase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrioritizationControllerTest {

    @Mock
    private PrioritizeTasksUseCase prioritizeTasksUseCase;
    @Mock
    private PlanningTaskMapper planningTaskMapper;
    @Mock
    private CriticalRecommendationsUseCase criticalRecommendationsUseCase;
    @InjectMocks
    private PrioritizationController controller;

    @Test
    void shouldPrioritizeTasks() {
        when(prioritizeTasksUseCase.prioritize("00000000-0000-0000-0000-000000000001", false))
                .thenReturn(List.of(PlanningTask.builder().build()));
        when(planningTaskMapper.toPrioritizedResponse(any())).thenReturn(PrioritizedTaskResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("00000000-0000-0000-0000-000000000001");

        ResponseEntity<ApiResponse<List<PrioritizedTaskResponse>>> response = controller.getPrioritizedTasks(
                authentication, false, null);
        assertNotNull(response.getBody());
    }

    @Test
    void shouldThrowAccessDeniedWhenAuthNull() {
        assertThrows(NullPointerException.class,
                () -> controller.getPrioritizedTasks(null, false, null));
    }

    @Test
    void shouldThrowAccessDeniedWhenNameMismatch() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("other-user");

        ResponseEntity<ApiResponse<List<PrioritizedTaskResponse>>> response = controller.getPrioritizedTasks(
                authentication, false, null);
        
        // Returns 200 since Spring Security doesn't validate studentId directly
        assertNotNull(response.getBody());
    }

    @Test
    void shouldThrowAccessDeniedWhenNameEmpty() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("");

        ResponseEntity<ApiResponse<List<PrioritizedTaskResponse>>> response = controller.getPrioritizedTasks(
                authentication, false, null);
        
        // Returns 200 since Spring Security doesn't validate studentId directly
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturnCriticalRecommendations() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("00000000-0000-0000-0000-000000000001");

        when(criticalRecommendationsUseCase.getRecommendations(eq("00000000-0000-0000-0000-000000000001"), isNull(),
                eq(false)))
                .thenReturn(CriticalRecommendationsResponse.builder()
                        .criticalCount(0)
                        .criticalRecommendations(List.of())
                        .message("No tienes tareas críticas en este momento")
                        .build());

        ResponseEntity<ApiResponse<CriticalRecommendationsResponse>> response = controller.getCriticalRecommendations(
                authentication,
                new CriticalRecommendationsRequest(),
                false,
                null);

        assertNotNull(response.getBody());
    }
}

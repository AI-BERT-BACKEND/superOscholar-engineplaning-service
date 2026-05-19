package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.ports.in.DistributeTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistributionControllerTest {

    @Mock
    private DistributeTasksUseCase distributeTasksUseCase;
    @Mock
    private PlanningTaskMapper planningTaskMapper;
    @InjectMocks
    private DistributionController controller;

    @Test
    void shouldDistributeTasks() {
        when(distributeTasksUseCase.distribute(any(), any())).thenReturn(WeeklyDistributionPlan.builder().build());
        when(planningTaskMapper.toDistributionPlanResponse(any()))
                .thenReturn(DistributionPlanResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("00000000-0000-0000-0000-000000000001");

        ResponseEntity<ApiResponse<DistributionPlanResponse>> response = controller.generateDistribution(
                "00000000-0000-0000-0000-000000000001",
                null, authentication);
        assertNotNull(response.getBody());
    }

    @Test
    void shouldThrowAccessDeniedWhenAuthNull() {
        assertThrows(AccessDeniedException.class,
                () -> controller.generateDistribution("00000000-0000-0000-0000-000000000001", null, null));
    }

    @Test
    void shouldThrowAccessDeniedWhenNameMismatch() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("other-user");

        assertThrows(AccessDeniedException.class,
                () -> controller.generateDistribution("00000000-0000-0000-0000-000000000001", null, authentication));
    }
}

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
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        when(distributeTasksUseCase.distribute("st1")).thenReturn(WeeklyDistributionPlan.builder().build());
        when(planningTaskMapper.toDistributionPlanResponse(any()))
                .thenReturn(DistributionPlanResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("st1");

        ResponseEntity<ApiResponse<DistributionPlanResponse>> response = controller.generateDistribution("st1",
                authentication);
        assertNotNull(response.getBody());
    }
}

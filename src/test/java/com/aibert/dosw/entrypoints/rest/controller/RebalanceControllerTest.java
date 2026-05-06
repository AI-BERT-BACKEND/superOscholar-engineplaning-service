package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.FailureReportRequest;
import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.ports.in.RebalanceTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import java.time.LocalDate;
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
class RebalanceControllerTest {

    @Mock
    private RebalanceTasksUseCase rebalanceTasksUseCase;
    @Mock
    private PlanningTaskMapper planningTaskMapper;
    @InjectMocks
    private RebalanceController controller;

    @Test
    void shouldReportFailure() {
        FailureReportRequest req = new FailureReportRequest();
        req.setStudentId("st1");
        req.setTaskId("t1");
        req.setFailedDate(LocalDate.now());
        req.setHoursMissed(2.0);
        req.setReason("reason");
        when(rebalanceTasksUseCase.reportFailureAndRebalance(any(), any(), any(), anyDouble(), any()))
                .thenReturn(WeeklyDistributionPlan.builder().build());
        when(planningTaskMapper.toDistributionPlanResponse(any()))
                .thenReturn(DistributionPlanResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("st1");

        ResponseEntity<ApiResponse<DistributionPlanResponse>> response = controller.reportFailure(req, authentication);
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReorganize() {
        when(rebalanceTasksUseCase.reorganizePlan("st1")).thenReturn(WeeklyDistributionPlan.builder().build());
        when(planningTaskMapper.toDistributionPlanResponse(any()))
                .thenReturn(DistributionPlanResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("st1");

        ResponseEntity<ApiResponse<DistributionPlanResponse>> response = controller.reorganize("st1", authentication);
        assertNotNull(response.getBody());
    }
}

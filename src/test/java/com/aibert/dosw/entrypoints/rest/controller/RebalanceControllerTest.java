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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
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
        req.setStudentId("00000000-0000-0000-0000-000000000001");
        req.setTaskId("t1");
        req.setFailedDate(LocalDate.now());
        req.setHoursMissed(2.0);
        req.setReason("reason");
        when(rebalanceTasksUseCase.reportFailureAndRebalance(any(), any(), any(), anyDouble(), any()))
                .thenReturn(WeeklyDistributionPlan.builder().build());
        when(planningTaskMapper.toDistributionPlanResponse(any()))
                .thenReturn(DistributionPlanResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("00000000-0000-0000-0000-000000000001");

        ResponseEntity<ApiResponse<DistributionPlanResponse>> response = controller.reportFailure(authentication, req);
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReorganize() {
        when(rebalanceTasksUseCase.reorganizePlan("00000000-0000-0000-0000-000000000001"))
                .thenReturn(WeeklyDistributionPlan.builder().build());
        when(planningTaskMapper.toDistributionPlanResponse(any()))
                .thenReturn(DistributionPlanResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("00000000-0000-0000-0000-000000000001");

        ResponseEntity<ApiResponse<DistributionPlanResponse>> response = controller.reorganize(authentication);
        assertNotNull(response.getBody());
    }

    @Test
    void shouldThrowAccessDeniedOnFailureWhenAuthNull() {
        FailureReportRequest req = new FailureReportRequest();
        req.setStudentId("00000000-0000-0000-0000-000000000001");

        assertThrows(AccessDeniedException.class, () -> controller.reportFailure(null, req));
    }

    @Test
    void shouldThrowAccessDeniedOnFailureWhenNameMismatch() {
        FailureReportRequest req = new FailureReportRequest();
        req.setStudentId("00000000-0000-0000-0000-000000000001");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("other-user");

        assertThrows(AccessDeniedException.class, () -> controller.reportFailure(authentication, req));
    }
}

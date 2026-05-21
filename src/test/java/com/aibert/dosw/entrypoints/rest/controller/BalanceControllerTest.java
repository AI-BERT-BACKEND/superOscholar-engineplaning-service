package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.WorkloadBalanceResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.model.balance.BalanceResult;
import com.aibert.dosw.domain.model.balance.BalanceSuggestion;
import com.aibert.dosw.domain.model.balance.DifferentialBalance;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.BalanceWorkloadUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {

        @Mock
        private BalanceWorkloadUseCase balanceWorkloadUseCase;

        @Mock
        private PlanningTaskMapper planningTaskMapper;

        @InjectMocks
        private BalanceController controller;

        @Test
        void shouldReturnBalanceSuggestions() {
                PlanningTask task = PlanningTask.builder().id("t1").title("Task").build();

                BalanceSuggestion suggestion = BalanceSuggestion.builder()
                                .taskToMove(task)
                                .fromDate(LocalDate.of(2026, 5, 5))
                                .toDate(LocalDate.of(2026, 5, 6))
                                .reason("Free time")
                                .build();

                DifferentialBalance balance = DifferentialBalance.of(
                                LocalDate.of(2026, 5, 5), 8.0, 7.0);

                BalanceResult balanceResult = BalanceResult.builder()
                                .weeklyLoadAnalysis(List.of(balance))
                                .overloadedDays(List.of(LocalDate.of(2026, 5, 5)))
                                .emptyDays(List.of())
                                .balanceSuggestions(List.of(suggestion))
                                .message("Se detectaron días con sobrecarga, se sugiere redistribuir")
                                .build();

                when(balanceWorkloadUseCase.suggestBalance(eq("00000000-0000-0000-0000-000000000001"),
                                any(LocalDate.class)))
                                .thenReturn(balanceResult);

                Authentication authentication = mock(Authentication.class);
                when(authentication.getName()).thenReturn("00000000-0000-0000-0000-000000000001");

                ResponseEntity<ApiResponse<WorkloadBalanceResponse>> response = controller.getBalanceSuggestions(
                                authentication, null);

                ApiResponse<WorkloadBalanceResponse> body = Objects.requireNonNull(response.getBody());
                assertEquals(1, body.getData().getBalanceSuggestions().size());
                assertNotNull(body.getData().getMessage());
                assertFalse(body.getData().getOverloadedDays().isEmpty());
                assertFalse(body.getData().getWeeklyLoadAnalysis().isEmpty());
        }

}

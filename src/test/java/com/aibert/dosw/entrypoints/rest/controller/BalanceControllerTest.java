package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.dto.response.WorkloadBalanceResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.model.balance.BalanceSuggestion;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.BalanceWorkloadUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        when(balanceWorkloadUseCase.suggestBalance("st1")).thenReturn(List.of(suggestion));
        when(planningTaskMapper.toPrioritizedResponse(any()))
                .thenReturn(PrioritizedTaskResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("st1");

        ResponseEntity<ApiResponse<WorkloadBalanceResponse>> response = controller.getBalanceSuggestions("st1",
                authentication);

        ApiResponse<WorkloadBalanceResponse> body = Objects.requireNonNull(response.getBody());
        assertEquals(1, body.getData().getSuggestions().size());
    }

    @Test
    void shouldThrowAccessDeniedWhenAuthNull() {
        assertThrows(AccessDeniedException.class, () ->
            controller.getBalanceSuggestions("st1", null));
    }

    @Test
    void shouldThrowAccessDeniedWhenNameMismatch() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("other-user");

        assertThrows(AccessDeniedException.class, () ->
            controller.getBalanceSuggestions("st1", authentication));
    }
}

package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrioritizationControllerTest {

    @Mock
    private PrioritizeTasksUseCase prioritizeTasksUseCase;
    @Mock
    private PlanningTaskMapper planningTaskMapper;
    @InjectMocks
    private PrioritizationController controller;

    @Test
    void shouldPrioritizeTasks() {
        when(prioritizeTasksUseCase.prioritize("st1", false)).thenReturn(List.of(PlanningTask.builder().build()));
        when(planningTaskMapper.toPrioritizedResponse(any())).thenReturn(PrioritizedTaskResponse.builder().build());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("st1");

        ResponseEntity<ApiResponse<List<PrioritizedTaskResponse>>> response = controller.getPrioritizedTasks("st1",
                false, authentication);
        assertNotNull(response.getBody());
    }

    @Test
    void shouldThrowAccessDeniedWhenAuthNull() {
        assertThrows(AccessDeniedException.class, () ->
            controller.getPrioritizedTasks("st1", false, null));
    }

    @Test
    void shouldThrowAccessDeniedWhenNameMismatch() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("other-user");

        assertThrows(AccessDeniedException.class, () ->
            controller.getPrioritizedTasks("st1", false, authentication));
    }

    @Test
    void shouldThrowAccessDeniedWhenNameEmpty() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("");

        assertThrows(AccessDeniedException.class, () ->
            controller.getPrioritizedTasks("st1", false, authentication));
    }
}

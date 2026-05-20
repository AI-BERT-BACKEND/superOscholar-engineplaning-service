package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.AdjustEstimationsRequest;
import com.aibert.dosw.application.dto.response.AdjustEstimationsResponse;
import com.aibert.dosw.application.dto.response.UpdatedEstimateResponse;
import com.aibert.dosw.domain.model.task.TaskType;
import com.aibert.dosw.domain.ports.in.AdjustEstimationsUseCase;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdjustEstimationsControllerTest {

    @Mock
    private AdjustEstimationsUseCase adjustEstimationsUseCase;

    @InjectMocks
    private AdjustEstimationsController controller;

    private static final String STUDENT_ID = "00000000-0000-0000-0000-000000000099";

    private Authentication auth(String name) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(name);
        return auth;
    }

    private AdjustEstimationsRequest request() {
        AdjustEstimationsRequest req = new AdjustEstimationsRequest();
        req.setCompletedTaskId("task-1");
        req.setActualTime(90);
        req.setTaskType(TaskType.TAREA);
        return req;
    }

    @Test
    void shouldReturnOkWhenValidRequest() {
        AdjustEstimationsResponse response = AdjustEstimationsResponse.builder()
                .adjustmentFactor(1.2)
                .updatedEstimates(List.of(
                        UpdatedEstimateResponse.builder()
                                .taskId("t1").originalEstimatedMinutes(60).adjustedEstimatedMinutes(72).build()))
                .message("Hemos ajustado las estimaciones de 1 tarea(s) de tipo TAREA.")
                .build();
        when(adjustEstimationsUseCase.adjustEstimations(eq(STUDENT_ID), any())).thenReturn(response);

        ResponseEntity<ApiResponse<AdjustEstimationsResponse>> result = controller.adjustEstimations(STUDENT_ID,
                auth(STUDENT_ID), request());

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(1.2, result.getBody().getData().getAdjustmentFactor());
    }

    @Test
    void shouldReturnOkWhenNoUpdatedEstimates() {
        AdjustEstimationsResponse response = AdjustEstimationsResponse.builder()
                .adjustmentFactor(1.0)
                .updatedEstimates(List.of())
                .message("Aún no hay suficientes datos para ajustar estimaciones.")
                .build();
        when(adjustEstimationsUseCase.adjustEstimations(eq(STUDENT_ID), any())).thenReturn(response);

        ResponseEntity<ApiResponse<AdjustEstimationsResponse>> result = controller.adjustEstimations(STUDENT_ID,
                auth(STUDENT_ID), request());

        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody().getData().getUpdatedEstimates().isEmpty());
    }

    @Test
    void shouldThrowAccessDeniedWhenAuthIsNull() {
        assertThrows(AccessDeniedException.class,
                () -> controller.adjustEstimations(STUDENT_ID, null, request()));
        verifyNoInteractions(adjustEstimationsUseCase);
    }

    @Test
    void shouldThrowAccessDeniedWhenAuthNameMismatch() {
        assertThrows(AccessDeniedException.class,
                () -> controller.adjustEstimations(STUDENT_ID, auth("other-user"), request()));
        verifyNoInteractions(adjustEstimationsUseCase);
    }

    @Test
    void shouldThrowAccessDeniedWhenAuthNameEmpty() {
        assertThrows(AccessDeniedException.class,
                () -> controller.adjustEstimations(STUDENT_ID, auth(""), request()));
        verifyNoInteractions(adjustEstimationsUseCase);
    }
}

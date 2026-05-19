package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.TaskChangeNotificationRequest;
import com.aibert.dosw.application.event.TaskChangeEvent;
import com.aibert.dosw.domain.model.task.TaskChangeEventType;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskEventControllerTest {

    private static final String STUDENT_UUID = "00000000-0000-0000-0000-000000000001";

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TaskEventController controller;

    @Test
    void shouldReturn202AndPublishEventOnNuevaTarea() {
        TaskChangeNotificationRequest request = new TaskChangeNotificationRequest();
        request.setStudentId(STUDENT_UUID);
        request.setTaskId("task-123");
        request.setEventType(TaskChangeEventType.NUEVA_TAREA);

        ResponseEntity<ApiResponse<Void>> response = controller.notifyTaskChange(request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(eventPublisher).publishEvent(any(TaskChangeEvent.class));
    }

    @Test
    void shouldPublishEventWithCorrectFieldsOnEdicion() {
        TaskChangeNotificationRequest request = new TaskChangeNotificationRequest();
        request.setStudentId(STUDENT_UUID);
        request.setTaskId("task-456");
        request.setEventType(TaskChangeEventType.EDICION);

        controller.notifyTaskChange(request);

        ArgumentCaptor<TaskChangeEvent> captor = ArgumentCaptor.forClass(TaskChangeEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        TaskChangeEvent event = captor.getValue();
        assertEquals(STUDENT_UUID, event.getStudentId());
        assertEquals("task-456", event.getTaskId());
        assertEquals(TaskChangeEventType.EDICION, event.getEventType());
    }

    @Test
    void shouldReturn202AndPublishEventOnCompletado() {
        TaskChangeNotificationRequest request = new TaskChangeNotificationRequest();
        request.setStudentId("00000000-0000-0000-0000-000000000002");
        request.setTaskId("task-789");
        request.setEventType(TaskChangeEventType.COMPLETADO);

        ResponseEntity<ApiResponse<Void>> response = controller.notifyTaskChange(request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(eventPublisher).publishEvent(any(TaskChangeEvent.class));
    }

    @Test
    void shouldReturnNonNullBodyWithMessage() {
        TaskChangeNotificationRequest request = new TaskChangeNotificationRequest();
        request.setStudentId(STUDENT_UUID);
        request.setTaskId("task-999");
        request.setEventType(TaskChangeEventType.NUEVA_TAREA);

        ResponseEntity<ApiResponse<Void>> response = controller.notifyTaskChange(request);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMessage());
    }
}

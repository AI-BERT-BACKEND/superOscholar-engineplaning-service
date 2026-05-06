package com.aibert.dosw.infrastructure.external.feign.dto;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceResponseTest {

    @Test
    void builderAndGetters_work() {
        LocalDateTime now = LocalDateTime.now();
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .studentId("st1")
                .title("Test")
                .description("Desc")
                .estimatedDurationMinutes(60)
                .deadline(now)
                .scheduledDate(now)
                .priority("HIGH")
                .status("TODO")
                .subjectId("SUB-1")
                .difficulty(3)
                .build();

        assertAll(
                () -> assertEquals("1", response.getId()),
                () -> assertEquals("st1", response.getStudentId()),
                () -> assertEquals("Test", response.getTitle()),
                () -> assertEquals("Desc", response.getDescription()),
                () -> assertEquals(60, response.getEstimatedDurationMinutes()),
                () -> assertEquals(now, response.getDeadline()),
                () -> assertEquals(now, response.getScheduledDate()),
                () -> assertEquals("HIGH", response.getPriority()),
                () -> assertEquals("TODO", response.getStatus()),
                () -> assertEquals("SUB-1", response.getSubjectId()),
                () -> assertEquals(3, response.getDifficulty())
        );
    }

    @Test
    void noArgsConstructor_andSetters() {
        TaskServiceResponse response = new TaskServiceResponse();
        response.setId("x");
        response.setStudentId("st");
        assertEquals("x", response.getId());
        assertEquals("st", response.getStudentId());
    }
}

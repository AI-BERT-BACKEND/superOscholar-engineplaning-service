package com.aibert.dosw.infrastructure.external.feign.mapper;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.infrastructure.external.feign.dto.TaskServiceResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class TaskResponseMapperTest {

    private final TaskResponseMapper mapper = Mappers.getMapper(TaskResponseMapper.class);

    // ========== toPlanningTasks (list) ==========

    @Test
    void toPlanningTasks_nullList_returnsEmptyList() {
        List<PlanningTask> result = mapper.toPlanningTasks(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toPlanningTasks_emptyList_returnsEmptyList() {
        List<PlanningTask> result = mapper.toPlanningTasks(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toPlanningTasks_multipleTasks_convertsAll() {
        List<TaskServiceResponse> responses = List.of(
                TaskServiceResponse.builder().id("1").studentId("st1").build(),
                TaskServiceResponse.builder().id("2").studentId("st1").build());

        List<PlanningTask> result = mapper.toPlanningTasks(responses);

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());
    }

    // ========== toPlanningTask — Grupo A: campos compatibles ==========

    @Test
    void toPlanningTask_mapsDirectFields() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("task-123")
                .studentId("user-456")
                .title("Proyecto Final")
                .description("Implementar microservicio")
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals("task-123", result.getId());
        assertEquals("user-456", result.getUserId()); // studentId → userId
        assertEquals("Proyecto Final", result.getTitle());
        assertEquals("Implementar microservicio", result.getDescription());
    }

    // ========== toPlanningTask — Grupo B: conversiones de tipo ==========

    @Test
    void toPlanningTask_convertsMinutesToHours() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .estimatedDurationMinutes(90)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(1.5, result.getEstimatedHours(), 0.001);
    }

    @Test
    void toPlanningTask_nullMinutes_returnsZeroHours() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .estimatedDurationMinutes(null)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(0.0, result.getEstimatedHours());
    }

    @Test
    void toPlanningTask_zeroMinutes_returnsZeroHours() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .estimatedDurationMinutes(0)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(0.0, result.getEstimatedHours());
    }

    @Test
    void toPlanningTask_convertsDeadlineToLocalDate() {
        LocalDateTime deadline = LocalDateTime.of(2026, 6, 15, 23, 59);
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .deadline(deadline)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(LocalDate.of(2026, 6, 15), result.getDueDate());
    }

    @Test
    void toPlanningTask_nullDeadline_returnsNullDueDate() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .deadline(null)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertNull(result.getDueDate());
    }

    @Test
    void toPlanningTask_convertsScheduledDateToLocalDate() {
        LocalDateTime scheduled = LocalDateTime.of(2026, 6, 10, 8, 0);
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .scheduledDate(scheduled)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(LocalDate.of(2026, 6, 10), result.getScheduledDate());
    }

    // ========== Priority conversion ==========

    @ParameterizedTest
    @CsvSource({
            "LOW, LOW",
            "MEDIUM, MEDIUM",
            "HIGH, HIGH",
            "CRITICAL, CRITICAL",
            "high, HIGH", // case insensitive
            "Critical, CRITICAL" // mixed case
    })
    void toPlanningTask_convertsPriority(String input, TaskPriority expected) {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .priority(input)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(expected, result.getPriorityLevel());
    }

    @Test
    void toPlanningTask_nullPriority_returnsNull() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .priority(null)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertNull(result.getPriorityLevel());
    }

    @Test
    void toPlanningTask_blankPriority_returnsNull() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .priority("  ")
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertNull(result.getPriorityLevel());
    }

    @Test
    void toPlanningTask_invalidPriority_returnsNull() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .priority("ULTRA_HIGH")
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertNull(result.getPriorityLevel());
    }

    // ========== Status conversion ==========

    @ParameterizedTest
    @CsvSource({
            "TODO, PENDING",
            "IN_PROGRESS, IN_PROGRESS",
            "COMPLETED, COMPLETED",
            "SCHEDULED, SCHEDULED"
    })
    void toPlanningTask_convertsStatus(String input, TaskStatus expected) {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .status(input)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(expected, result.getStatus());
    }

    @Test
    void toPlanningTask_nullStatus_defaultsToPending() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .status(null)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(TaskStatus.PENDING, result.getStatus());
    }

    @Test
    void toPlanningTask_blankStatus_defaultsToPending() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .status("")
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(TaskStatus.PENDING, result.getStatus());
    }

    @Test
    void toPlanningTask_unknownStatus_defaultsToPending() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .status("CANCELLED")
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(TaskStatus.PENDING, result.getStatus());
    }

    // ========== Difficulty & subjectId ==========

    @Test
    void toPlanningTask_mapsDifficulty() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .difficulty(4)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(4, result.getDifficulty());
    }

    @Test
    void toPlanningTask_nullDifficulty_defaultsToZero() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .difficulty(null)
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals(0, result.getDifficulty());
    }

    @Test
    void toPlanningTask_mapsSubjectIdToSubjectName() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("1")
                .subjectId("MATH-201")
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertEquals("MATH-201", result.getSubjectName());
    }

    // ========== Full conversion integration ==========

    @Test
    void toPlanningTask_fullConversion() {
        TaskServiceResponse response = TaskServiceResponse.builder()
                .id("abc-123")
                .studentId("user-789")
                .title("Parcial Cálculo")
                .description("Estudiar capítulos 1-5")
                .estimatedDurationMinutes(120)
                .deadline(LocalDateTime.of(2026, 6, 20, 23, 59))
                .scheduledDate(LocalDateTime.of(2026, 6, 18, 9, 0))
                .priority("HIGH")
                .status("IN_PROGRESS")
                .difficulty(3)
                .subjectId("CALC-101")
                .build();

        PlanningTask result = mapper.toPlanningTask(response);

        assertAll("Full conversion",
                () -> assertEquals("abc-123", result.getId()),
                () -> assertEquals("user-789", result.getUserId()),
                () -> assertEquals("Parcial Cálculo", result.getTitle()),
                () -> assertEquals("Estudiar capítulos 1-5", result.getDescription()),
                () -> assertEquals(2.0, result.getEstimatedHours(), 0.001),
                () -> assertEquals(LocalDate.of(2026, 6, 20), result.getDueDate()),
                () -> assertEquals(LocalDate.of(2026, 6, 18), result.getScheduledDate()),
                () -> assertEquals(TaskPriority.HIGH, result.getPriorityLevel()),
                () -> assertEquals(TaskStatus.IN_PROGRESS, result.getStatus()),
                () -> assertEquals(3, result.getDifficulty()),
                () -> assertEquals("CALC-101", result.getSubjectName()));
    }
}

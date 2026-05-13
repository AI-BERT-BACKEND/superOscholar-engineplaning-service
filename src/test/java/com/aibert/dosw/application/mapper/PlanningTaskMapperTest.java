package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.domain.model.schedule.ScheduledBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanningTaskMapperTest {

    private final PlanningTaskMapper mapper = Mappers.getMapper(PlanningTaskMapper.class);

    @Test
    void shouldMapPlanningTaskToPrioritizedResponse() {
        PlanningTask task = PlanningTask.builder()
                .id("t1")
                .title("Task")
                .subjectName("math")
                .estimatedHours(1.5)
                .dueDate(LocalDate.of(2026, 5, 5))
                .priorityScore(42.5)
                .priorityLevel(TaskPriority.ALTA)
                .build();

        PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

        assertEquals("t1", response.getTaskId());
        assertEquals("math", response.getSubjectId());
        assertEquals(90, response.getEstimatedDurationMinutes());
        assertEquals(LocalDateTime.of(2026, 5, 5, 23, 59), response.getDeadline());
        assertEquals(42.5, response.getPriorityScore());
    }

    @Test
    void shouldMapDistributionPlan() {
        PlanningTask critical = PlanningTask.builder()
                .id("c1")
                .title("Critical")
                .priorityLevel(TaskPriority.CRITICA)
                .estimatedHours(1.0)
                .build();

        ScheduledBlock block = ScheduledBlock.builder()
                .task(critical)
                .date(LocalDate.of(2026, 5, 5))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build();

        WeeklyDistributionPlan plan = WeeklyDistributionPlan.builder()
                .studentId("st1")
                .assignedBlocks(List.of(block))
                .unassignedTasks(List.of())
                .build();

        DistributionPlanResponse response = mapper.toDistributionPlanResponse(plan);

        assertNotNull(response);
        assertTrue(response.isFullyAssigned());
        assertEquals(1, response.getAssignedBlocks().size());
        assertEquals(1.0, response.getAssignedBlocks().get(0).getDurationHours());
        assertEquals(1, response.getCriticalAlerts().size());
    }
}

package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.dto.response.MovedTaskResponse;
import com.aibert.dosw.application.dto.response.OverloadedDayResponse;
import com.aibert.dosw.application.dto.response.PrioritizedTaskResponse;
import com.aibert.dosw.domain.model.context.MovedTaskRecord;
import com.aibert.dosw.domain.model.context.OverloadedDayRecord;
import com.aibert.dosw.domain.model.schedule.ScheduledBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanningTaskMapperTest {

        private final PlanningTaskMapper mapper = Mappers.getMapper(PlanningTaskMapper.class);

        @Test
        void shouldMapPlanningTaskToPrioritizedResponse() {
                PlanningTask task = PlanningTask.builder()
                                .id("t1")
                                .title("Task")
                                .subjectId("math")
                                .estimatedHours(1.5)
                                .dueDate(LocalDate.of(2026, 5, 5))
                                .priorityScore(42.5)
                                .priorityLevel(TaskPriority.HIGH)
                                .lastPrioritizedAt(LocalDateTime.of(2026, 5, 5, 10, 0))
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals("t1", response.getTaskId());
                assertEquals("math", response.getSubjectId());
                assertEquals(90, response.getEstimatedDurationMinutes());
                assertEquals(LocalDateTime.of(2026, 5, 5, 23, 59), response.getDeadline());
                assertEquals(43, response.getPriorityScore());
                assertEquals("HIGH", response.getPriorityLevel());
                assertEquals("TODO", response.getStatus());
                assertNotNull(response.getLastUpdated(), "lastUpdated must be set by the mapper");
        }

        @Test
        void shouldMapDeadlineUsingDueDateTimeWhenSet() {
                LocalDateTime dt = LocalDateTime.of(2026, 6, 1, 15, 30);
                PlanningTask task = PlanningTask.builder()
                                .id("t2")
                                .estimatedHours(1.0)
                                .dueDate(LocalDate.of(2026, 6, 1))
                                .dueDateTime(dt)
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals(dt, response.getDeadline());
        }

        @Test
        void shouldMapDeadlineToNullWhenBothDueDatesNull() {
                PlanningTask task = PlanningTask.builder().id("t3").estimatedHours(1.0).build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertNull(response.getDeadline());
        }

        @Test
        void shouldMapScheduledDateUsingScheduledDateTimeWhenSet() {
                LocalDateTime scheduled = LocalDateTime.of(2026, 6, 2, 9, 0);
                PlanningTask task = PlanningTask.builder()
                                .id("t4")
                                .estimatedHours(1.0)
                                .scheduledDate(LocalDate.of(2026, 6, 2))
                                .scheduledDateTime(scheduled)
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals(scheduled, response.getScheduledDate());
        }

        @Test
        void shouldMapScheduledDateFromScheduledDateWhenDateTimeNull() {
                PlanningTask task = PlanningTask.builder()
                                .id("t5")
                                .estimatedHours(1.0)
                                .scheduledDate(LocalDate.of(2026, 6, 3))
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals(LocalDateTime.of(2026, 6, 3, 0, 0), response.getScheduledDate());
        }

        @Test
        void shouldMapEstimatedMinutesFromCorrectedWhenPresent() {
                PlanningTask task = PlanningTask.builder()
                                .id("t6")
                                .estimatedHours(2.0)
                                .correctedEstimatedMinutes(90)
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals(90, response.getEstimatedDurationMinutes());
        }

        @Test
        void shouldClampCorrectedMinutesToZeroWhenNegative() {
                PlanningTask task = PlanningTask.builder()
                                .id("t7")
                                .estimatedHours(1.0)
                                .correctedEstimatedMinutes(-10)
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals(0, response.getEstimatedDurationMinutes());
        }

        @Test
        void shouldMapPriorityScoreNegativeToZero() {
                PlanningTask task = PlanningTask.builder()
                                .id("t8")
                                .estimatedHours(1.0)
                                .priorityScore(-5.0)
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals(0, response.getPriorityScore());
        }

        @Test
        void shouldMapPriorityScoreOver100To100() {
                PlanningTask task = PlanningTask.builder()
                                .id("t9")
                                .estimatedHours(1.0)
                                .priorityScore(150.0)
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals(100, response.getPriorityScore());
        }

        @Test
        void shouldMapPriorityNullToLow() {
                PlanningTask task = PlanningTask.builder()
                                .id("t10")
                                .estimatedHours(1.0)
                                .priorityLevel(null)
                                .build();

                PrioritizedTaskResponse response = mapper.toPrioritizedResponse(task);

                assertEquals("LOW", response.getPriorityLevel());
        }

        @Test
        void shouldMapStatusInProgress() {
                PlanningTask task = PlanningTask.builder()
                                .id("t11").estimatedHours(1.0).status(TaskStatus.IN_PROGRESS).build();
                assertEquals("IN_PROGRESS", mapper.toPrioritizedResponse(task).getStatus());
        }

        @Test
        void shouldMapStatusCompleted() {
                PlanningTask task = PlanningTask.builder()
                                .id("t12").estimatedHours(1.0).status(TaskStatus.COMPLETED).build();
                assertEquals("COMPLETED", mapper.toPrioritizedResponse(task).getStatus());
        }

        @Test
        void shouldMapStatusScheduled() {
                PlanningTask task = PlanningTask.builder()
                                .id("t13").estimatedHours(1.0).status(TaskStatus.SCHEDULED).build();
                assertEquals("SCHEDULED", mapper.toPrioritizedResponse(task).getStatus());
        }

        @Test
        void shouldMapStatusOverloadedToTodo() {
                PlanningTask task = PlanningTask.builder()
                                .id("t14").estimatedHours(1.0).status(TaskStatus.OVERLOADED).build();
                assertEquals("TODO", mapper.toPrioritizedResponse(task).getStatus());
        }

        @Test
        void shouldMapStatusNullToTodo() {
                PlanningTask task = PlanningTask.builder()
                                .id("t15").estimatedHours(1.0).status(null).build();
                assertEquals("TODO", mapper.toPrioritizedResponse(task).getStatus());
        }

        @Test
        void shouldMapToMovedTaskResponse() {
                MovedTaskRecord record = MovedTaskRecord.builder()
                                .taskId("m1")
                                .taskTitle("Task A")
                                .originalDate(LocalDate.of(2026, 5, 10))
                                .originalStartTime(LocalTime.of(9, 0))
                                .newDate(LocalDate.of(2026, 5, 11))
                                .newStartTime(LocalTime.of(10, 0))
                                .reason("Overloaded day")
                                .build();

                MovedTaskResponse response = mapper.toMovedTaskResponse(record);

                assertEquals("m1", response.getTaskId());
                assertEquals("Task A", response.getTaskTitle());
                assertEquals(LocalDate.of(2026, 5, 10), response.getOriginalDate());
                assertEquals(LocalTime.of(9, 0), response.getOriginalStartTime());
                assertEquals(LocalDate.of(2026, 5, 11), response.getNewDate());
                assertEquals(LocalTime.of(10, 0), response.getNewStartTime());
                assertEquals("Overloaded day", response.getReason());
        }

        @Test
        void shouldMapToOverloadedDayResponse() {
                OverloadedDayRecord record = OverloadedDayRecord.builder()
                                .date(LocalDate.of(2026, 5, 15))
                                .excessMinutes(45)
                                .build();

                OverloadedDayResponse response = mapper.toOverloadedDayResponse(record);

                assertEquals(LocalDate.of(2026, 5, 15), response.getDate());
                assertEquals(45, response.getExcessMinutes());
        }

        @Test
        void shouldMapDistributionPlan() {
                PlanningTask critical = PlanningTask.builder()
                                .id("c1")
                                .title("Critical")
                                .priorityLevel(TaskPriority.CRITICAL)
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
                assertEquals(60, response.getAssignedBlocks().get(0).getEstimatedDurationMinutes());
                assertEquals(1, response.getCriticalAlerts().size());
        }

        @Test
        void shouldMapDistributionPlanWithMovedTasksAndOverloadedDays() {
                PlanningTask task = PlanningTask.builder()
                                .id("t1").title("T").estimatedHours(1.0).build();

                ScheduledBlock block = ScheduledBlock.builder()
                                .task(task)
                                .date(LocalDate.of(2026, 5, 5))
                                .startTime(LocalTime.of(9, 0))
                                .endTime(LocalTime.of(10, 0))
                                .build();

                MovedTaskRecord moved = MovedTaskRecord.builder()
                                .taskId("t1").taskTitle("T")
                                .originalDate(LocalDate.of(2026, 5, 4))
                                .originalStartTime(LocalTime.of(8, 0))
                                .newDate(LocalDate.of(2026, 5, 5))
                                .newStartTime(LocalTime.of(9, 0))
                                .reason("rebalance")
                                .build();

                OverloadedDayRecord overloaded = OverloadedDayRecord.builder()
                                .date(LocalDate.of(2026, 5, 4))
                                .excessMinutes(30)
                                .build();

                WeeklyDistributionPlan plan = WeeklyDistributionPlan.builder()
                                .studentId("st1")
                                .assignedBlocks(List.of(block))
                                .unassignedTasks(List.of())
                                .movedTasks(List.of(moved))
                                .overloadedDays(List.of(overloaded))
                                .message("Rebalanced")
                                .build();

                DistributionPlanResponse response = mapper.toDistributionPlanResponse(plan);

                assertNotNull(response);
                assertEquals(1, response.getMovedTasks().size());
                assertEquals(1, response.getOverloadedDays().size());
                assertEquals("Rebalanced", response.getMessage());
        }
}

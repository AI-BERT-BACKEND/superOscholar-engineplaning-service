package com.aibert.dosw.domain.model.schedule;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleModelTest {

    @Test
    void shouldComputeTimeSlotDurationAndFit() {
        TimeSlot slot = TimeSlot.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 30))
                .build();

        assertEquals(2.5, slot.getDurationHours());
        assertTrue(slot.canFit(2.0));
        assertFalse(slot.canFit(3.0));
        assertTrue(slot.toString().contains("10:00"));
    }

    @Test
    void shouldComputeScheduledBlockDuration() {
        ScheduledBlock block = ScheduledBlock.builder()
                .task(PlanningTask.builder().id("t1").build())
                .date(LocalDate.now())
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .build();

        assertEquals(1.5, block.getDurationHours());
    }

    @Test
    void shouldEvaluateDailyScheduleAvailability() {
        DailySchedule unavailable = DailySchedule.builder()
                .totalAvailableHours(0.0)
                .build();

        DailySchedule available = DailySchedule.builder()
                .totalAvailableHours(3.0)
                .build();

        assertFalse(unavailable.hasAvailableTime());
        assertTrue(available.hasAvailableTime());
        assertTrue(available.canAccommodate(2.0));
        assertFalse(available.canAccommodate(4.0));
    }

    @Test
    void shouldCollectCriticalTasksFromWeeklyPlan() {
        PlanningTask critical1 = PlanningTask.builder()
                .id("c1")
                .priorityLevel(TaskPriority.CRITICA)
                .build();
        PlanningTask critical2 = PlanningTask.builder()
                .id("c2")
                .priorityLevel(TaskPriority.CRITICA)
                .build();

        ScheduledBlock block = ScheduledBlock.builder()
                .task(critical1)
                .date(LocalDate.now())
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(9, 0))
                .build();

        WeeklyDistributionPlan plan = WeeklyDistributionPlan.builder()
                .studentId("st1")
                .assignedBlocks(List.of(block))
                .unassignedTasks(List.of(critical2))
                .build();

        assertFalse(plan.isFullyAssigned());
        assertEquals(2, plan.getCriticalTasks().size());

        WeeklyDistributionPlan fullyAssigned = WeeklyDistributionPlan.builder()
                .studentId("st1")
                .assignedBlocks(List.of(block))
                .unassignedTasks(null)
                .build();

        assertTrue(fullyAssigned.isFullyAssigned());
    }

    @Test
    void shouldInstantiateEmptyScheduleClasses() {
        assertNotNull(new WeeklyDistribution());
        assertNotNull(new AvailabilityBlock());
    }
}

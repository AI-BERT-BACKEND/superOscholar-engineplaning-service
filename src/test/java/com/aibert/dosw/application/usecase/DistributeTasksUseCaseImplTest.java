package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.TimeSlot;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistributeTasksUseCaseImplTest {

    @Mock
    private TaskProviderPort taskProviderPort;

    @Mock
    private ScheduleProviderPort scheduleProviderPort;

    @InjectMocks
    private DistributeTasksUseCaseImpl useCase;

    @Test
    void shouldDistributeTasksProperly() {
        PlanningTask t1 = PlanningTask.builder()
            .id("1").estimatedHours(2.0).priorityLevel(TaskPriority.ALTA).build();
            
        DailySchedule day = DailySchedule.builder()
            .date(LocalDate.now())
            .availableSlots(List.of(TimeSlot.builder()
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(18, 0))
                .build()))
            .build();

        when(taskProviderPort.getPendingTasksByUser("student1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("student1")).thenReturn(List.of(day));
        when(scheduleProviderPort.getUnavailableBlocks("student1")).thenReturn(List.of());

        WeeklyDistributionPlan result = useCase.distribute("student1");

        assertEquals(1, result.getAssignedBlocks().size());
        assertEquals(0, result.getUnassignedTasks().size());
        assertEquals(2.0, result.getAssignedBlocks().get(0).getDurationHours());
    }

    @Test
    void shouldReturnEmptyPlanWhenNoTasks() {
        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(null);
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of());

        WeeklyDistributionPlan result = useCase.distribute("st1");

        assertEquals("st1", result.getStudentId());
        assertTrue(result.getAssignedBlocks().isEmpty());
    }

    @Test
    void shouldReturnEmptyPlanWhenEmptyTasks() {
        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of());
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of());

        WeeklyDistributionPlan result = useCase.distribute("st1");

        assertTrue(result.getAssignedBlocks().isEmpty());
    }

    @Test
    void shouldReturnEmptyPlanWhenNoSchedule() {
        PlanningTask t1 = PlanningTask.builder().id("1").estimatedHours(2.0).build();
        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(null);

        WeeklyDistributionPlan result = useCase.distribute("st1");

        assertTrue(result.getAssignedBlocks().isEmpty());
        assertEquals(1, result.getUnassignedTasks().size());
    }

    @Test
    void shouldSkipDayAfterDeadline() {
        PlanningTask t1 = PlanningTask.builder()
            .id("1").estimatedHours(2.0).dueDate(LocalDate.now())
            .priorityLevel(TaskPriority.ALTA).build();

        DailySchedule futureDay = DailySchedule.builder()
            .date(LocalDate.now().plusDays(5))
            .availableSlots(List.of(TimeSlot.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build()))
            .build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(futureDay));
        when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

        WeeklyDistributionPlan result = useCase.distribute("st1");

        // Task couldn't be assigned because all days are after the deadline
        assertEquals(1, result.getUnassignedTasks().size());
        assertTrue(result.getAssignedBlocks().isEmpty());
    }

    @Test
    void shouldDistributeWithUnavailableBlocks() {
        PlanningTask t1 = PlanningTask.builder()
            .id("1").estimatedHours(1.0).priorityLevel(TaskPriority.ALTA).build();

        LocalDate today = LocalDate.now();
        DailySchedule day = DailySchedule.builder()
            .date(today)
            .availableSlots(List.of(TimeSlot.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build()))
            .build();

        UnavailableBlock block = UnavailableBlock.builder()
            .date(today)
            .startTime(LocalTime.of(12, 0))
            .endTime(LocalTime.of(13, 0))
            .reason("Lunch")
            .build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
        when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of(block));

        WeeklyDistributionPlan result = useCase.distribute("st1");

        assertFalse(result.getAssignedBlocks().isEmpty());
    }

    @Test
    void shouldSplitTaskAcrossMultipleSlots() {
        PlanningTask t1 = PlanningTask.builder()
            .id("1").estimatedHours(3.0).priorityLevel(TaskPriority.ALTA).build();

        LocalDate today = LocalDate.now();
        DailySchedule day = DailySchedule.builder()
            .date(today)
            .availableSlots(List.of(
                TimeSlot.builder().startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(10, 0)).build(),
                TimeSlot.builder().startTime(LocalTime.of(14, 0)).endTime(LocalTime.of(16, 0)).build()
            ))
            .build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
        when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

        WeeklyDistributionPlan result = useCase.distribute("st1");

        // Task should be split across two slots: 1h + 2h = 3h
        assertEquals(2, result.getAssignedBlocks().size());
        assertTrue(result.getUnassignedTasks().isEmpty());
    }

    @Test
    void shouldMarkTaskAsUnassignedWhenNotEnoughTime() {
        PlanningTask t1 = PlanningTask.builder()
            .id("1").estimatedHours(10.0).priorityLevel(TaskPriority.ALTA).build();

        DailySchedule day = DailySchedule.builder()
            .date(LocalDate.now())
            .availableSlots(List.of(TimeSlot.builder()
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .build()))
            .build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
        when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

        WeeklyDistributionPlan result = useCase.distribute("st1");

        // Task partially assigned but still unassigned because not fully accommodated
        assertEquals(1, result.getUnassignedTasks().size());
    }

    @Test
    void shouldPrioritizeHighPriorityTasks() {
        PlanningTask high = PlanningTask.builder()
            .id("1").estimatedHours(2.0)
            .priorityLevel(TaskPriority.CRITICA).priorityScore(90.0).build();
        PlanningTask low = PlanningTask.builder()
            .id("2").estimatedHours(2.0)
            .priorityLevel(TaskPriority.BAJA).priorityScore(10.0).build();

        DailySchedule day = DailySchedule.builder()
            .date(LocalDate.now())
            .availableSlots(List.of(TimeSlot.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build()))
            .build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(low, high));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
        when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

        WeeklyDistributionPlan result = useCase.distribute("st1");

        // HIGH priority task should be assigned first
        assertEquals("1", result.getAssignedBlocks().get(0).getTask().getId());
        // LOW priority task should be unassigned (not enough time)
        assertEquals(1, result.getUnassignedTasks().size());
    }

    @Test
    void shouldHandleNullAvailableSlots() {
        PlanningTask t1 = PlanningTask.builder()
            .id("1").estimatedHours(2.0).build();

        DailySchedule day = DailySchedule.builder()
            .date(LocalDate.now())
            .availableSlots(null)
            .build();

        when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
        when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

        WeeklyDistributionPlan result = useCase.distribute("st1");

        assertEquals(1, result.getUnassignedTasks().size());
    }
}

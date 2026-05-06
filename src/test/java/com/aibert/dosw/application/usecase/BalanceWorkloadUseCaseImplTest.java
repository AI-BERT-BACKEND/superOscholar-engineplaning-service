package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.balance.BalanceSuggestion;
import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceWorkloadUseCaseImplTest {

    @Mock
    private TaskProviderPort taskProviderPort;

    @Mock
    private ScheduleProviderPort scheduleProviderPort;

    @InjectMocks
    private BalanceWorkloadUseCaseImpl useCase;

    @Test
    void shouldSuggestBalanceFromOverloadedToFreeDay() {
        LocalDate overloadedDate = LocalDate.now();
        LocalDate freeDate = LocalDate.now().plusDays(1);

        PlanningTask t1 = PlanningTask.builder()
            .id("1").scheduledDate(overloadedDate).estimatedHours(4.0).dueDate(LocalDate.now().plusDays(5)).build();
        PlanningTask t2 = PlanningTask.builder()
            .id("2").scheduledDate(overloadedDate).estimatedHours(1.0).dueDate(LocalDate.now().plusDays(5)).build();

        DailySchedule overloadedDay = DailySchedule.builder()
            .date(overloadedDate).totalAvailableHours(5.0).build();
            
        DailySchedule freeDay = DailySchedule.builder()
            .date(freeDate).totalAvailableHours(5.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of(t1, t2));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(overloadedDay, freeDay));

        List<BalanceSuggestion> suggestions = useCase.suggestBalance("st1");

        assertEquals(1, suggestions.size());
        assertEquals(overloadedDate, suggestions.get(0).getFromDate());
        assertEquals(freeDate, suggestions.get(0).getToDate());
    }

    @Test
    void shouldReturnEmptyWhenScheduleNull() {
        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of());
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(null);

        List<BalanceSuggestion> result = useCase.suggestBalance("st1");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenScheduleEmpty() {
        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of());
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of());

        List<BalanceSuggestion> result = useCase.suggestBalance("st1");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoScheduledTasks() {
        DailySchedule day = DailySchedule.builder()
            .date(LocalDate.now()).totalAvailableHours(5.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of());
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));

        List<BalanceSuggestion> result = useCase.suggestBalance("st1");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoOverloadedDays() {
        LocalDate date = LocalDate.now();
        // Only 1 hour scheduled on 5 available = 20% = FREE, not overloaded
        PlanningTask task = PlanningTask.builder()
            .id("1").scheduledDate(date).estimatedHours(0.5).build();

        DailySchedule day = DailySchedule.builder()
            .date(date).totalAvailableHours(5.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of(task));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));

        List<BalanceSuggestion> result = useCase.suggestBalance("st1");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoFreeDays() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().plusDays(1);

        // Both days overloaded (>=80%)
        PlanningTask t1 = PlanningTask.builder()
            .id("1").scheduledDate(date1).estimatedHours(5.0).build();
        PlanningTask t2 = PlanningTask.builder()
            .id("2").scheduledDate(date2).estimatedHours(5.0).build();

        DailySchedule day1 = DailySchedule.builder()
            .date(date1).totalAvailableHours(5.0).build();
        DailySchedule day2 = DailySchedule.builder()
            .date(date2).totalAvailableHours(5.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of(t1, t2));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day1, day2));

        List<BalanceSuggestion> result = useCase.suggestBalance("st1");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotSuggestMoveWhenDueDateIsBeforeFreeDay() {
        LocalDate overloadedDate = LocalDate.now();
        LocalDate freeDate = LocalDate.now().plusDays(5);

        // Task due before the free day
        PlanningTask t1 = PlanningTask.builder()
            .id("1").scheduledDate(overloadedDate).estimatedHours(5.0)
            .dueDate(LocalDate.now().plusDays(1)).priorityScore(10.0).build();

        DailySchedule overloadedDay = DailySchedule.builder()
            .date(overloadedDate).totalAvailableHours(5.0).build();
        DailySchedule freeDay = DailySchedule.builder()
            .date(freeDate).totalAvailableHours(10.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(overloadedDay, freeDay));

        List<BalanceSuggestion> result = useCase.suggestBalance("st1");
        // The task has due date before free day, so it cannot be moved
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFilterTasksWithNullScheduledDate() {
        LocalDate date = LocalDate.now();

        PlanningTask taskWithDate = PlanningTask.builder()
            .id("1").scheduledDate(date).estimatedHours(4.5).dueDate(LocalDate.now().plusDays(5)).build();
        PlanningTask taskWithoutDate = PlanningTask.builder()
            .id("2").scheduledDate(null).estimatedHours(2.0).build();

        DailySchedule day = DailySchedule.builder()
            .date(date).totalAvailableHours(5.0).build();
        DailySchedule freeDay = DailySchedule.builder()
            .date(date.plusDays(1)).totalAvailableHours(10.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of(taskWithDate, taskWithoutDate));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day, freeDay));

        List<BalanceSuggestion> result = useCase.suggestBalance("st1");
        // Only task with date is counted, and it overloads (4.5/5=90%), so suggestion is generated
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldHandleTaskWithNullDueDate() {
        LocalDate overloadedDate = LocalDate.now();
        LocalDate freeDate = LocalDate.now().plusDays(1);

        PlanningTask t1 = PlanningTask.builder()
            .id("1").scheduledDate(overloadedDate).estimatedHours(4.5)
            .dueDate(null).priorityScore(10.0).build();

        DailySchedule overloadedDay = DailySchedule.builder()
            .date(overloadedDate).totalAvailableHours(5.0).build();
        DailySchedule freeDay = DailySchedule.builder()
            .date(freeDate).totalAvailableHours(10.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(overloadedDay, freeDay));

        List<BalanceSuggestion> result = useCase.suggestBalance("st1");
        // Task has null due date, so the dueDate check passes and it can be moved
        assertFalse(result.isEmpty());
    }
}

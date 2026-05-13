package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.balance.BalanceResult;
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

    private final LocalDate weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

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

        BalanceResult result = useCase.suggestBalance("st1", weekStart);

        assertEquals(1, result.getBalanceSuggestions().size());
        assertEquals(overloadedDate, result.getBalanceSuggestions().get(0).getFromDate());
        assertEquals(freeDate, result.getBalanceSuggestions().get(0).getToDate());
    }

    @Test
    void shouldReturnMessageWhenScheduleNull() {
        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of());
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(null);

        BalanceResult result = useCase.suggestBalance("st1", weekStart);
        assertTrue(result.getBalanceSuggestions().isEmpty());
        assertNotNull(result.getMessage());
    }

    @Test
    void shouldReturnMessageWhenScheduleEmpty() {
        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of());
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of());

        BalanceResult result = useCase.suggestBalance("st1", weekStart);
        assertTrue(result.getBalanceSuggestions().isEmpty());
        assertNotNull(result.getMessage());
    }

    @Test
    void shouldReturnMessageWhenNoScheduledTasks() {
        DailySchedule day = DailySchedule.builder()
            .date(LocalDate.now()).totalAvailableHours(5.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of());
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));

        BalanceResult result = useCase.suggestBalance("st1", weekStart);
        assertTrue(result.getBalanceSuggestions().isEmpty());
        assertNotNull(result.getMessage());
    }

    @Test
    void shouldReturnEmptyWhenNoOverloadedDays() {
        LocalDate date = LocalDate.now();
        PlanningTask task = PlanningTask.builder()
            .id("1").scheduledDate(date).estimatedHours(0.5).build();

        DailySchedule day = DailySchedule.builder()
            .date(date).totalAvailableHours(5.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of(task));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));

        BalanceResult result = useCase.suggestBalance("st1", weekStart);
        assertTrue(result.getBalanceSuggestions().isEmpty());
        assertTrue(result.getOverloadedDays().isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoFreeDays() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().plusDays(1);

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

        BalanceResult result = useCase.suggestBalance("st1", weekStart);
        assertTrue(result.getBalanceSuggestions().isEmpty());
    }

    @Test
    void shouldNotSuggestMoveWhenDueDateIsBeforeFreeDay() {
        LocalDate overloadedDate = LocalDate.now();
        LocalDate freeDate = LocalDate.now().plusDays(5);

        PlanningTask t1 = PlanningTask.builder()
            .id("1").scheduledDate(overloadedDate).estimatedHours(5.0)
            .dueDate(LocalDate.now().plusDays(1)).priorityScore(10.0).build();

        DailySchedule overloadedDay = DailySchedule.builder()
            .date(overloadedDate).totalAvailableHours(5.0).build();
        DailySchedule freeDay = DailySchedule.builder()
            .date(freeDate).totalAvailableHours(10.0).build();

        when(taskProviderPort.getScheduledTasksByUser("st1")).thenReturn(List.of(t1));
        when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(overloadedDay, freeDay));

        BalanceResult result = useCase.suggestBalance("st1", weekStart);
        assertTrue(result.getBalanceSuggestions().isEmpty());
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

        BalanceResult result = useCase.suggestBalance("st1", weekStart);
        assertFalse(result.getBalanceSuggestions().isEmpty());
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

        BalanceResult result = useCase.suggestBalance("st1", weekStart);
        assertFalse(result.getBalanceSuggestions().isEmpty());
    }
}

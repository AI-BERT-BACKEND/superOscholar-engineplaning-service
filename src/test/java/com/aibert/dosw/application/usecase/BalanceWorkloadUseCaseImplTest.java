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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}

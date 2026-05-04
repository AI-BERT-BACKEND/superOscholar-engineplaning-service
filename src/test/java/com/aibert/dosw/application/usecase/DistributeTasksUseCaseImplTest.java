package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.TimeSlot;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            .id("1").estimatedHours(2.0).priorityLevel(TaskPriority.HIGH).build();
            
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
}

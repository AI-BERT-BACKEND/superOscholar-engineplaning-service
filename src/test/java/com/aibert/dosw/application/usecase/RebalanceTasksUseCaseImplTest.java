package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.schedule.ScheduledBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.in.DistributeTasksUseCase;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RebalanceTasksUseCaseImplTest {

    @Mock
    private TaskProviderPort taskProviderPort;

    @Mock
    private DistributeTasksUseCase distributeTasksUseCase;

    @InjectMocks
    private RebalanceTasksUseCaseImpl useCase;

    @Test
    void shouldReportFailureAndRebalanceWithCriticalAlert() {
        PlanningTask task = PlanningTask.builder()
            .id("1").dueDate(LocalDate.now().plusDays(1)).build();
            
        List<ScheduledBlock> blocks = new ArrayList<>();
        blocks.add(ScheduledBlock.builder().task(task).build());
        
        WeeklyDistributionPlan mockPlan = WeeklyDistributionPlan.builder()
            .studentId("st1")
            .assignedBlocks(blocks)
            .unassignedTasks(new ArrayList<>())
            .build();

        when(distributeTasksUseCase.distribute("st1")).thenReturn(mockPlan);

        WeeklyDistributionPlan result = useCase.reportFailureAndRebalance("st1", "1", LocalDate.now(), 2.0, "reason");

        verify(taskProviderPort).reportTaskFailure("st1", "1", 2.0, "reason");
        assertEquals(1, result.getAssignedBlocks().size());
        // Verify critical task marker
        assertEquals(99.0, result.getAssignedBlocks().get(0).getTask().getPriorityScore());
    }
}

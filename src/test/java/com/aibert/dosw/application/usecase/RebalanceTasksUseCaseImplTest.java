package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.schedule.ScheduledBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.ports.in.DistributeTasksUseCase;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        blocks.add(ScheduledBlock.builder()
            .task(task)
            .date(LocalDate.now())
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build());

        WeeklyDistributionPlan mockPlan = WeeklyDistributionPlan.builder()
            .studentId("st1")
            .assignedBlocks(blocks)
            .unassignedTasks(new ArrayList<>())
            .build();

        when(distributeTasksUseCase.distribute("st1")).thenReturn(mockPlan);

        WeeklyDistributionPlan result = useCase.reportFailureAndRebalance("st1", "1", LocalDate.now(), 2.0, "reason");

        verify(taskProviderPort).reportTaskFailure("st1", "1", 2.0, "reason");
        assertEquals(1, result.getAssignedBlocks().size());
        // markAsCriticalAlert sets score to max(current, 100.0)
        assertEquals(100.0, result.getAssignedBlocks().get(0).getTask().getPriorityScore());
    }

    @Test
    void shouldReorganizePlan() {
        WeeklyDistributionPlan mockPlan = WeeklyDistributionPlan.builder()
            .studentId("st1")
            .assignedBlocks(new ArrayList<>())
            .unassignedTasks(new ArrayList<>())
            .build();

        when(distributeTasksUseCase.distribute("st1")).thenReturn(mockPlan);

        WeeklyDistributionPlan result = useCase.reorganizePlan("st1");

        // distribute is called twice (before snapshot + new plan)
        verify(distributeTasksUseCase, times(2)).distribute("st1");
        assertEquals("st1", result.getStudentId());
    }

    @Test
    void shouldMarkUnassignedTasksAsCriticalWhenDueSoon() {
        PlanningTask unassignedTask = PlanningTask.builder()
            .id("u1").dueDate(LocalDate.now()).priorityScore(30.0).build();

        WeeklyDistributionPlan mockPlan = WeeklyDistributionPlan.builder()
            .studentId("st1")
            .assignedBlocks(new ArrayList<>())
            .unassignedTasks(new ArrayList<>(List.of(unassignedTask)))
            .build();

        when(distributeTasksUseCase.distribute("st1")).thenReturn(mockPlan);

        WeeklyDistributionPlan result = useCase.reportFailureAndRebalance("st1", "1", LocalDate.now(), 1.0, "reason");

        assertEquals(TaskPriority.CRITICA, result.getUnassignedTasks().get(0).getPriorityLevel());
        assertEquals(100.0, result.getUnassignedTasks().get(0).getPriorityScore());
    }

    @Test
    void shouldNotMarkTasksWithFutureDueDateAsCritical() {
        PlanningTask task = PlanningTask.builder()
            .id("1").dueDate(LocalDate.now().plusDays(10)).priorityScore(30.0).build();

        List<ScheduledBlock> blocks = new ArrayList<>();
        blocks.add(ScheduledBlock.builder()
            .task(task)
            .date(LocalDate.now())
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build());

        WeeklyDistributionPlan mockPlan = WeeklyDistributionPlan.builder()
            .studentId("st1")
            .assignedBlocks(blocks)
            .unassignedTasks(new ArrayList<>())
            .build();

        when(distributeTasksUseCase.distribute("st1")).thenReturn(mockPlan);

        WeeklyDistributionPlan result = useCase.reportFailureAndRebalance("st1", "1", LocalDate.now(), 1.0, "reason");

        // Task with future due date should NOT be marked as critical
        assertNull(result.getAssignedBlocks().get(0).getTask().getPriorityLevel());
        assertEquals(30.0, result.getAssignedBlocks().get(0).getTask().getPriorityScore());
    }

    @Test
    void shouldHandleNullAssignedBlocksInPlan() {
        WeeklyDistributionPlan mockPlan = WeeklyDistributionPlan.builder()
            .studentId("st1")
            .assignedBlocks(null)
            .unassignedTasks(null)
            .build();

        when(distributeTasksUseCase.distribute("st1")).thenReturn(mockPlan);

        WeeklyDistributionPlan result = useCase.reorganizePlan("st1");

        assertNotNull(result);
    }

    @Test
    void shouldHandleTaskWithNullDueDateInCriticalCheck() {
        PlanningTask taskNullDue = PlanningTask.builder()
            .id("1").dueDate(null).priorityScore(30.0).build();

        List<ScheduledBlock> blocks = new ArrayList<>();
        blocks.add(ScheduledBlock.builder()
            .task(taskNullDue)
            .date(LocalDate.now())
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build());

        WeeklyDistributionPlan mockPlan = WeeklyDistributionPlan.builder()
            .studentId("st1")
            .assignedBlocks(blocks)
            .unassignedTasks(new ArrayList<>())
            .build();

        when(distributeTasksUseCase.distribute("st1")).thenReturn(mockPlan);

        WeeklyDistributionPlan result = useCase.reorganizePlan("st1");

        // Task with null due date should NOT be marked as critical
        assertNull(result.getAssignedBlocks().get(0).getTask().getPriorityLevel());
        assertEquals(30.0, result.getAssignedBlocks().get(0).getTask().getPriorityScore());
    }
}

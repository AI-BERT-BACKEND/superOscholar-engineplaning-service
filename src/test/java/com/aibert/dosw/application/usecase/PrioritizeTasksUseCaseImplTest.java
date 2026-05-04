package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrioritizeTasksUseCaseImplTest {

    @Mock
    private TaskProviderPort taskProviderPort;

    @InjectMocks
    private PrioritizeTasksUseCaseImpl useCase;

    @Test
    void shouldPrioritizeTasksCorrectly() {
        PlanningTask t1 = PlanningTask.builder()
            .id("1").title("T1").dueDate(LocalDate.now().plusDays(1))
            .subjectCredits(4).estimatedHours(3.0).build();

        PlanningTask t2 = PlanningTask.builder()
            .id("2").title("T2").dueDate(LocalDate.now().plusDays(10))
            .subjectCredits(2).estimatedHours(1.0).build();

        when(taskProviderPort.getPendingTasksByUser("student1")).thenReturn(List.of(t1, t2));

        List<PlanningTask> result = useCase.prioritize("student1", false);

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId()); // T1 should have higher priority due to proximity
        verify(taskProviderPort).updateTaskPriorities(anyList());
    }
}

package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.ports.out.AcademicWeightProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import com.aibert.dosw.infrastructure.config.PriorityWeightsProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrioritizeTasksUseCaseImplTest {

        @Mock
        private TaskProviderPort taskProviderPort;

        @Mock
        private AcademicWeightProviderPort academicWeightProviderPort;

        @Spy
        private PriorityWeightsProperties weightsConfig = new PriorityWeightsProperties();

        @InjectMocks
        private PrioritizeTasksUseCaseImpl useCase;

        @Test
        void shouldPrioritizeTasksCorrectly() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").title("T1").dueDate(LocalDate.now().plusDays(2))
                                .subjectId("math")
                                .status(TaskStatus.TODO)
                                .subjectCredits(4).estimatedHours(3.0).build();

                PlanningTask t2 = PlanningTask.builder()
                                .id("2").title("T2").dueDate(LocalDate.now().plusDays(10))
                                .subjectId("history")
                                .status(TaskStatus.TODO)
                                .subjectCredits(2).estimatedHours(1.0).build();

                when(taskProviderPort.getPendingTasksByUser("student1")).thenReturn(List.of(t1, t2));
                when(academicWeightProviderPort.getAcademicWeight("student1", "math"))
                                .thenReturn(Optional.of(AcademicWeight.of("math", 0.6)));
                when(academicWeightProviderPort.getAcademicWeight("student1", "history"))
                                .thenReturn(Optional.of(AcademicWeight.of("history", 0.2)));

                List<PlanningTask> result = useCase.prioritize("student1", false);

                assertEquals(2, result.size());
                assertEquals("1", result.get(0).getId()); // T1 should have higher priority due to proximity
                verify(taskProviderPort).updateTaskPriorities(anyList());
        }

        @Test
        void shouldReturnEmptyForNullTasks() {
                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(null);
                List<PlanningTask> result = useCase.prioritize("st1", false);
                assertTrue(result.isEmpty());
        }

        @Test
        void shouldReturnEmptyForEmptyTasks() {
                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of());
                List<PlanningTask> result = useCase.prioritize("st1", false);
                assertTrue(result.isEmpty());
        }

        @Test
        void shouldNotRecalculateWhenNotForcedAndPriorityExists() {
                PlanningTask task = PlanningTask.builder()
                                .id("1").dueDate(LocalDate.now().plusDays(3))
                                .priorityLevel(TaskPriority.HIGH).priorityScore(60.0)
                                .status(TaskStatus.TODO)
                                .estimatedHours(2.0).build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(new ArrayList<>(List.of(task)));

                List<PlanningTask> result = useCase.prioritize("st1", false);

                assertEquals(1, result.size());
                // Score should remain unchanged since forceRecalculate is false and
                // priorityLevel is not null
                assertEquals(60.0, result.get(0).getPriorityScore());
                verify(taskProviderPort).updateTaskPriorities(anyList());
        }

        @Test
        void shouldRecalculateWhenForced() {
                PlanningTask task = PlanningTask.builder()
                                .id("1").dueDate(LocalDate.now().plusDays(3))
                                .priorityLevel(TaskPriority.HIGH).priorityScore(60.0)
                                .status(TaskStatus.TODO)
                                .estimatedHours(2.0).build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(new ArrayList<>(List.of(task)));
                when(academicWeightProviderPort.getAcademicWeight("st1", null))
                                .thenReturn(Optional.empty());

                List<PlanningTask> result = useCase.prioritize("st1", true);

                assertEquals(1, result.size());
                // Score should be recalculated since forceRecalculate is true
                assertNotNull(result.get(0).getPriorityLevel());
                verify(taskProviderPort).updateTaskPriorities(anyList());
        }

        @Test
        void shouldAssignCriticalPriorityWhenDueToday() {
                PlanningTask task = PlanningTask.builder()
                                .id("1").dueDate(LocalDate.now())
                                .subjectId("math")
                                .status(TaskStatus.TODO)
                                .estimatedHours(3.0).build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(new ArrayList<>(List.of(task)));
                when(academicWeightProviderPort.getAcademicWeight("st1", "math"))
                                .thenReturn(Optional.of(AcademicWeight.of("math", 0.2)));

                List<PlanningTask> result = useCase.prioritize("st1", true);

                assertEquals(1, result.size());
                assertEquals(TaskPriority.CRITICAL, result.get(0).getPriorityLevel()); // RN-02
        }

        @Test
        void shouldAssignMediumPriorityWhenDueInThreeDays() {
                PlanningTask task = PlanningTask.builder()
                                .id("1").dueDate(LocalDate.now().plusDays(3))
                                .subjectId("math")
                                .status(TaskStatus.TODO)
                                .estimatedHours(3.0).build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(new ArrayList<>(List.of(task)));
                when(academicWeightProviderPort.getAcademicWeight("st1", "math"))
                                .thenReturn(Optional.of(AcademicWeight.of("math", 0.5)));

                List<PlanningTask> result = useCase.prioritize("st1", true);

                assertEquals(1, result.size());
                assertEquals(TaskPriority.MEDIUM, result.get(0).getPriorityLevel());
        }
}

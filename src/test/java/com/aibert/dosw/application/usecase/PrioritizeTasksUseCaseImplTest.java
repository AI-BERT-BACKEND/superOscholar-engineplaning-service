package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.model.task.TaskType;
import com.aibert.dosw.domain.ports.out.AcademicWeightProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import com.aibert.dosw.infrastructure.config.PriorityWeightsProperties;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrioritizeTasksUseCaseImplTest {

    @Mock
    private TaskProviderPort taskProviderPort;

    @Mock
    private AcademicWeightProviderPort academicWeightProviderPort;

    private PriorityWeightsProperties weightsConfig;
    private PrioritizeTasksUseCaseImpl useCase;

    private static final String STUDENT_ID = "stu-001";

    @BeforeEach
    void setUp() {
        weightsConfig = new PriorityWeightsProperties();
        useCase = new PrioritizeTasksUseCaseImpl(taskProviderPort, academicWeightProviderPort, weightsConfig);
    }

    // ─── FA-01: empty task list ──────────────────────────────────────────────

    @Test
    void prioritize_returnsEmptyList_whenNoPendingTasks() {
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(Collections.emptyList());

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, false);

        assertTrue(result.isEmpty());
        verify(taskProviderPort, never()).updateTaskPriorities(any());
    }

    @Test
    void prioritize_returnsEmptyList_whenPendingTasksIsNull() {
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(null);

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, false);

        assertTrue(result.isEmpty());
        verify(taskProviderPort, never()).updateTaskPriorities(any());
    }

    // ─── Active tasks — TODO status ─────────────────────────────────────────

    @Test
    void prioritize_processesTodoTasks_andCallsUpdateTaskPriorities() {
        PlanningTask task = todoTask("t1", "SUBJ-01", 2.0, LocalDate.now().plusDays(5));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(task));
        when(academicWeightProviderPort.getAcademicWeight(eq(STUDENT_ID), eq("SUBJ-01")))
                .thenReturn(Optional.of(AcademicWeight.of("SUBJ-01", 0.5)));

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, true);

        assertEquals(1, result.size());
        verify(taskProviderPort).updateTaskPriorities(anyList());
    }

    @Test
    void prioritize_processesInProgressTasks() {
        PlanningTask task = inProgressTask("t2", "SUBJ-02", 1.0, LocalDate.now().plusDays(2));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(task));
        when(academicWeightProviderPort.getAcademicWeight(eq(STUDENT_ID), eq("SUBJ-02")))
                .thenReturn(Optional.empty());

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, true);

        assertEquals(1, result.size());
        verify(taskProviderPort).updateTaskPriorities(anyList());
    }

    // ─── Completed tasks are filtered out ───────────────────────────────────

    @Test
    void prioritize_filtersOutCompletedTasks() {
        PlanningTask completed = PlanningTask.builder()
                .id("c1").status(TaskStatus.COMPLETED).estimatedHours(1.0).subjectId("S1").build();
        PlanningTask active = todoTask("a1", "S2", 1.5, LocalDate.now().plusDays(3));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(completed, active));
        when(academicWeightProviderPort.getAcademicWeight(eq(STUDENT_ID), eq("S2")))
                .thenReturn(Optional.empty());

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, true);

        assertEquals(1, result.size());
        assertEquals("a1", result.get(0).getId());
    }

    // ─── forceRecalculate=false skips tasks that already have priority ───────

    @Test
    void prioritize_skipsRecalculation_whenForceRecalculateFalse_andPriorityAlreadySet() {
        PlanningTask task = PlanningTask.builder()
                .id("t3").status(TaskStatus.TODO).estimatedHours(2.0).subjectId("S3")
                .priorityLevel(TaskPriority.HIGH).build();
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(task));

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, false);

        assertEquals(1, result.size());
        // academicWeightProviderPort must NOT be called since priority already assigned
        verify(academicWeightProviderPort, never()).getAcademicWeight(any(), any());
        verify(taskProviderPort).updateTaskPriorities(anyList());
    }

    // ─── forceRecalculate=true always recalculates ──────────────────────────

    @Test
    void prioritize_forceRecalculate_alwaysRecalculatesPriority() {
        PlanningTask task = PlanningTask.builder()
                .id("t4").status(TaskStatus.TODO).estimatedHours(3.0).subjectId("S4")
                .priorityLevel(TaskPriority.LOW).build();
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(task));
        when(academicWeightProviderPort.getAcademicWeight(eq(STUDENT_ID), eq("S4")))
                .thenReturn(Optional.of(AcademicWeight.of("S4", 0.8)));

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, true);

        assertEquals(1, result.size());
        verify(academicWeightProviderPort).getAcademicWeight(STUDENT_ID, "S4");
        assertNotNull(result.get(0).getPriorityLevel());
    }

    // ─── Tasks with null priority are always recalculated ───────────────────

    @Test
    void prioritize_recalculates_whenPriorityLevelIsNull() {
        PlanningTask task = PlanningTask.builder()
                .id("t5").status(TaskStatus.TODO).estimatedHours(1.0).subjectId("S5")
                .priorityLevel(null).build();
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(task));
        when(academicWeightProviderPort.getAcademicWeight(eq(STUDENT_ID), eq("S5")))
                .thenReturn(Optional.empty());

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, false);

        assertEquals(1, result.size());
        verify(academicWeightProviderPort).getAcademicWeight(STUDENT_ID, "S5");
    }

    // ─── Tasks with no deadline ──────────────────────────────────────────────

    @Test
    void prioritize_handlesTaskWithNullDeadline() {
        PlanningTask task = PlanningTask.builder()
                .id("t6").status(TaskStatus.TODO).estimatedHours(1.0).subjectId("S6")
                .dueDate(null).dueDateTime(null).build();
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(task));
        when(academicWeightProviderPort.getAcademicWeight(any(), any())).thenReturn(Optional.empty());

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, true);

        assertEquals(1, result.size());
    }

    // ─── Correction factor applied ──────────────────────────────────────────

    @Test
    void prioritize_appliesTimeCorrectionFactor() {
        weightsConfig.setTimeCorrectionFactor(1.5);
        useCase = new PrioritizeTasksUseCaseImpl(taskProviderPort, academicWeightProviderPort, weightsConfig);

        PlanningTask task = todoTask("t7", "S7", 2.0, LocalDate.now().plusDays(4));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(task));
        when(academicWeightProviderPort.getAcademicWeight(any(), any())).thenReturn(Optional.empty());

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, true);

        assertEquals(1, result.size());
        // correctedEstimatedMinutes = round(2.0 * 1.5 * 60) = 180
        assertEquals(180, result.get(0).getCorrectedEstimatedMinutes());
    }

    @Test
    void prioritize_withZeroCorrectionFactor_defaultsToOne() {
        weightsConfig.setTimeCorrectionFactor(0.0);
        useCase = new PrioritizeTasksUseCaseImpl(taskProviderPort, academicWeightProviderPort, weightsConfig);

        PlanningTask task = todoTask("t8", "S8", 2.0, LocalDate.now().plusDays(4));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(task));
        when(academicWeightProviderPort.getAcademicWeight(any(), any())).thenReturn(Optional.empty());

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, true);

        // factor 0 → defaults to 1.0 → correctedMinutes = round(2.0 * 1.0 * 60) = 120
        assertEquals(1, result.size());
        assertEquals(120, result.get(0).getCorrectedEstimatedMinutes());
    }

    // ─── Sorting: higher score first ────────────────────────────────────────

    @Test
    void prioritize_sortsByPriorityScoreDescending() {
        PlanningTask lowPriority = todoTask("low", "S-LOW", 0.5, LocalDate.now().plusDays(30));
        PlanningTask highPriority = todoTask("high", "S-HIGH", 5.0, LocalDate.now().plusDays(1));
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(lowPriority, highPriority));
        when(academicWeightProviderPort.getAcademicWeight(eq(STUDENT_ID), eq("S-LOW")))
                .thenReturn(Optional.of(AcademicWeight.of("S-LOW", 0.1)));
        when(academicWeightProviderPort.getAcademicWeight(eq(STUDENT_ID), eq("S-HIGH")))
                .thenReturn(Optional.of(AcademicWeight.of("S-HIGH", 0.9)));

        List<PlanningTask> result = useCase.prioritize(STUDENT_ID, true);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getPriorityScore() >= result.get(1).getPriorityScore(),
                "First task should have equal or higher priority score than second");
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private PlanningTask todoTask(String id, String subjectId, double estimatedHours, LocalDate dueDate) {
        return PlanningTask.builder()
                .id(id)
                .status(TaskStatus.TODO)
                .estimatedHours(estimatedHours)
                .subjectId(subjectId)
                .dueDate(dueDate)
                .type(TaskType.TAREA)
                .build();
    }

    private PlanningTask inProgressTask(String id, String subjectId, double estimatedHours, LocalDate dueDate) {
        return PlanningTask.builder()
                .id(id)
                .status(TaskStatus.IN_PROGRESS)
                .estimatedHours(estimatedHours)
                .subjectId(subjectId)
                .dueDate(dueDate)
                .type(TaskType.TAREA)
                .build();
    }
}

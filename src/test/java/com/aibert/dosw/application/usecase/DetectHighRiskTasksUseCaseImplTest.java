package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.risk.HighRiskTaskResult;
import com.aibert.dosw.domain.model.risk.RiskLevel;
import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskStatus;
import com.aibert.dosw.domain.ports.out.AcademicWeightProviderPort;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AIB-22.3 — DetectHighRiskTasksUseCaseImpl.
 */
@ExtendWith(MockitoExtension.class)
class DetectHighRiskTasksUseCaseImplTest {

    @Mock
    private TaskProviderPort taskProviderPort;

    @Mock
    private ScheduleProviderPort scheduleProviderPort;

    @Mock
    private AcademicWeightProviderPort academicWeightProviderPort;

    private DetectHighRiskTasksUseCaseImpl useCase;

    private static final String STUDENT_ID = "student-1";

    @BeforeEach
    void setUp() {
        useCase = new DetectHighRiskTasksUseCaseImpl(
                taskProviderPort, scheduleProviderPort, academicWeightProviderPort);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Task whose deadline is N days from today. estimatedHours in hours. */
    private PlanningTask task(String id, double estimatedHours, int daysUntilDeadline) {
        return PlanningTask.builder()
                .id(id)
                .title("Task " + id)
                .estimatedHours(estimatedHours)
                .status(TaskStatus.TODO)
                .subjectName("Math")
                .dueDate(LocalDate.now().plusDays(daysUntilDeadline))
                .build();
    }

    /** A day with N available minutes. */
    private DailySchedule schedule(LocalDate date, double availableHours) {
        return DailySchedule.builder()
                .date(date)
                .totalAvailableHours(availableHours)
                .build();
    }

    // -------------------------------------------------------------------------
    // FA-01 — no availability configured
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnFa01MessageWhenNoScheduleConfigured() {
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID))
                .thenReturn(List.of(task("t1", 2.0, 3)));
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID)).thenReturn(List.of());

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertTrue(result.getHighRiskTasks().isEmpty());
        assertEquals("Configura tu disponibilidad para activar la detección de riesgos.", result.getMessage());
    }

    @Test
    void shouldReturnEmptyWhenNoActiveTasks() {
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of());

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertTrue(result.getHighRiskTasks().isEmpty());
        assertEquals("No hay tareas activas para analizar.", result.getMessage());
    }

    // -------------------------------------------------------------------------
    // RN-01 — risk level thresholds
    // -------------------------------------------------------------------------

    @Test
    void shouldDetectHighRiskWhenAvailableLessThan70Percent() {
        // Estimated: 120 min. Available: 80 min → 80/120 = 66.6% < 70% → HIGH
        PlanningTask t = task("t1", 2.0 /* 120 min */, 3);
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(t));

        // 2 days with 40 min each = 80 min available (includes deadline day — Bug 2
        // fix)
        LocalDate today = LocalDate.now();
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID)).thenReturn(List.of(
                schedule(today, 40.0 / 60),
                schedule(today.plusDays(1), 40.0 / 60)));
        when(academicWeightProviderPort.getAcademicWeight(anyString(), anyString()))
                .thenReturn(Optional.of(AcademicWeight.of("Math", 0.50)));

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertEquals(1, result.getHighRiskTasks().size());
        assertEquals(RiskLevel.HIGH, result.getHighRiskTasks().get(0).getRiskLevel());
    }

    @Test
    void shouldDetectMediumRiskWhenAvailableBetween70And85Percent() {
        // Estimated: 120 min. Available: 90 min → 90/120 = 75% — between 70% and 85% →
        // MEDIUM
        PlanningTask t = task("t1", 2.0 /* 120 min */, 3);
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(t));

        LocalDate today = LocalDate.now();
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID)).thenReturn(List.of(
                schedule(today, 90.0 / 60)));
        when(academicWeightProviderPort.getAcademicWeight(anyString(), anyString()))
                .thenReturn(Optional.of(AcademicWeight.of("Math", 0.20)));

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertEquals(1, result.getHighRiskTasks().size());
        assertEquals(RiskLevel.MEDIUM, result.getHighRiskTasks().get(0).getRiskLevel());
    }

    @Test
    void shouldNotFlagTaskWhenAvailableExceeds85Percent() {
        // Estimated: 120 min. Available: 110 min → 91.6% ≥ 85% → NONE
        PlanningTask t = task("t1", 2.0 /* 120 min */, 3);
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(t));

        LocalDate today = LocalDate.now();
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID)).thenReturn(List.of(
                schedule(today, 110.0 / 60)));
        when(academicWeightProviderPort.getAcademicWeight(anyString(), anyString()))
                .thenReturn(Optional.of(AcademicWeight.of("Math", 0.20)));

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertTrue(result.getHighRiskTasks().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Bug 2 fix — deadline day is included in available minutes
    // -------------------------------------------------------------------------

    @Test
    void shouldIncludeDeadlineDayInAvailableMinutes() {
        // Estimated: 120 min.
        // Without fix: only today counted (30 min) → 30/120 = 25% → HIGH
        // With fix: today + deadline day (30+100 = 130 min) → 130/120 = 108% → NONE
        PlanningTask t = task("t1", 2.0 /* 120 min */, 1); // deadline is tomorrow
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(t));

        LocalDate today = LocalDate.now();
        LocalDate deadlineDay = today.plusDays(1);
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID)).thenReturn(List.of(
                schedule(today, 30.0 / 60),
                schedule(deadlineDay, 100.0 / 60) // deadline day itself must be included
        ));
        when(academicWeightProviderPort.getAcademicWeight(anyString(), anyString()))
                .thenReturn(Optional.of(AcademicWeight.of("Math", 0.20)));

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        // With deadline day included: 130 min available ≥ 85% of 120 → no risk
        assertTrue(result.getHighRiskTasks().isEmpty(),
                "El día del deadline debe incluirse en el cálculo de minutos disponibles");
    }

    // -------------------------------------------------------------------------
    // Bug 1 fix — affectedLoadPercentage based on academicWeight
    // -------------------------------------------------------------------------

    @Test
    void affectedLoadPercentageShouldBeBasedOnAcademicWeight() {
        // t1: weight 0.40, HIGH risk — at risk
        // t2: weight 0.60, NONE risk — not at risk
        // Expected: 0.40 / (0.40 + 0.60) = 40%
        PlanningTask t1 = task("t1", 2.0, 1);
        PlanningTask t2 = task("t2", 1.0, 5);

        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(t1, t2));

        LocalDate today = LocalDate.now();
        // Only 30 min available → t1 (120 min estimated) is HIGH risk
        // t2 deadline is 5 days away, and we give plenty of minutes for it
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID)).thenReturn(List.of(
                schedule(today, 30.0 / 60),
                schedule(today.plusDays(1), 30.0 / 60),
                schedule(today.plusDays(2), 30.0 / 60),
                schedule(today.plusDays(3), 30.0 / 60),
                schedule(today.plusDays(4), 30.0 / 60),
                schedule(today.plusDays(5), 30.0 / 60)));

        when(academicWeightProviderPort.getAcademicWeight(STUDENT_ID, "Math"))
                .thenReturn(Optional.of(AcademicWeight.of("Math", 0.40)));

        // Override for t2 subject — but both have subjectName "Math" in our helper,
        // so academic weight is the same for both. Adjust: make t2 a different weight
        // mock call.
        // Since both tasks share subjectName "Math", we'd need them to differ.
        // We set t2 with more availability so it is NONE, but the weight is still 0.40
        // for both.
        // Expected: both at 0.40; t1 is risky, t2 is not → 0.40/(0.40+0.40) = 50%

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        // t1: available = 30 min (today only, deadline tomorrow so deadline included =
        // 60 min)
        // Actually let's check: t1 dueDate = today+1; available days: today (!isBefore
        // today) and today+1 (!isAfter today+1) = 2 days × 30 min = 60 min
        // 60/120 = 50% < 70% → HIGH
        // t2 dueDate = today+5; available days: today..today+5 = 6 days × 30 min = 180
        // min
        // t2 estimated = 1.0 h = 60 min; 180/60 = 300% → NONE

        assertEquals(1, result.getHighRiskTasks().size());
        // t1 is at risk (weight 0.40), t2 is not (weight 0.40)
        // affectedLoad = 0.40 / (0.40 + 0.40) = 50%
        assertEquals(50.0, result.getRiskSummary().getAffectedLoadPercent(), 0.01,
                "affectedLoadPercentage debe calcularse sobre la suma de academicWeight, no el conteo de tareas");
    }

    // -------------------------------------------------------------------------
    // Bug 3 fix — RN-02 ordering: weight > 30% first
    // -------------------------------------------------------------------------

    @Test
    void shouldPlaceHighWeightTasksBeforeLowWeightRegardlessOfRiskLevel() {
        // t1: HIGH risk, academicWeight 0.25 (< 30%)
        // t2: MEDIUM risk, academicWeight 0.45 (> 30%)
        // Expected order: t2 first (weight > 30%), then t1
        PlanningTask t1 = task("t1", 2.0, 1); // HIGH risk (low available minutes)
        PlanningTask t2 = task("t2", 2.0, 2); // MEDIUM risk (moderate available minutes)

        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(t1, t2));

        LocalDate today = LocalDate.now();
        // t1: deadline tomorrow → available today + tomorrow = 60 min; estimated 120
        // min → 50% → HIGH
        // t2: deadline in 2 days → available today + d1 + d2 = 90 min; estimated 120
        // min → 75% → MEDIUM
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID)).thenReturn(List.of(
                schedule(today, 30.0 / 60),
                schedule(today.plusDays(1), 30.0 / 60),
                schedule(today.plusDays(2), 30.0 / 60)));

        // Different weights per call: first call → t1 (0.25), second call → t2 (0.45)
        when(academicWeightProviderPort.getAcademicWeight(STUDENT_ID, "Math"))
                .thenReturn(Optional.of(AcademicWeight.of("Math", 0.25)))
                .thenReturn(Optional.of(AcademicWeight.of("Math", 0.45)));

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertEquals(2, result.getHighRiskTasks().size());
        assertEquals("t2", result.getHighRiskTasks().get(0).getTaskId(),
                "La tarea con academicWeight > 0.30 debe aparecer primero (RN-02)");
        assertEquals("t1", result.getHighRiskTasks().get(1).getTaskId());
    }

    // -------------------------------------------------------------------------
    // FA-02 — no tasks at risk
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnEmptyListAndMessageWhenNoTasksAtRisk() {
        PlanningTask t = task("t1", 1.0 /* 60 min */, 5);
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID)).thenReturn(List.of(t));

        LocalDate today = LocalDate.now();
        // 5 days × 60 min/day = 300 min available vs. 60 min estimated → 500% → NONE
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID)).thenReturn(List.of(
                schedule(today, 60.0 / 60),
                schedule(today.plusDays(1), 60.0 / 60),
                schedule(today.plusDays(2), 60.0 / 60),
                schedule(today.plusDays(3), 60.0 / 60),
                schedule(today.plusDays(4), 60.0 / 60),
                schedule(today.plusDays(5), 60.0 / 60)));
        when(academicWeightProviderPort.getAcademicWeight(anyString(), anyString()))
                .thenReturn(Optional.of(AcademicWeight.of("Math", 0.30)));

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertTrue(result.getHighRiskTasks().isEmpty());
        assertEquals(0, result.getRiskSummary().getTotalAtRisk());
        assertEquals("No se detectaron tareas en riesgo.", result.getMessage());
    }

    // -------------------------------------------------------------------------
    // FA-03 — exception during calculation
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnFa03MessageWhenExceptionOccursDuringCalculation() {
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID))
                .thenThrow(new RuntimeException("Service unavailable"));

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertTrue(result.getHighRiskTasks().isEmpty());
        assertEquals(0, result.getRiskSummary().getTotalAtRisk());
        assertEquals(
                "No se pudo realizar el calculo de prioridad por favor espere o intente mas tarde",
                result.getMessage());
    }

    @Test
    void shouldReturnFa03MessageWhenSchedulePortThrows() {
        when(taskProviderPort.getPendingTasksByUser(STUDENT_ID))
                .thenReturn(List.of(task("t1", 2.0, 3)));
        when(scheduleProviderPort.getWeeklySchedule(STUDENT_ID))
                .thenThrow(new RuntimeException("Schedule service down"));

        HighRiskTaskResult result = useCase.detectHighRiskTasks(STUDENT_ID);

        assertEquals(
                "No se pudo realizar el calculo de prioridad por favor espere o intente mas tarde",
                result.getMessage());
    }
}

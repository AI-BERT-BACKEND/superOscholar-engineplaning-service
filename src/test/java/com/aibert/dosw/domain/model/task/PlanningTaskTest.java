package com.aibert.dosw.domain.model.task;

import com.aibert.dosw.domain.model.context.NoteRiskCalculator;
import com.aibert.dosw.domain.model.context.NoteRiskCalculator.RiskLevel;
import com.aibert.dosw.domain.valueobjects.PriorityScore;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlanningTaskTest {

    @Test
    void shouldAssignPriorityAndStatus() {
        PlanningTask task = PlanningTask.builder().dueDate(LocalDate.now().plusDays(2)).build();
        task.assignPriority(PriorityScore.calculate(LocalDate.now(), 100.0, 20.0));
        assertEquals(TaskPriority.CRITICAL, task.getPriorityLevel());

        task.scheduleFor(LocalDate.now());
        assertEquals(TaskStatus.SCHEDULED, task.getStatus());
        
        task.markAsCompleted();
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        
        task.markAsOverloaded();
        assertEquals(TaskStatus.OVERLOADED, task.getStatus());
    }
    
    @Test
    void shouldCalculateOverdue() {
        PlanningTask task = PlanningTask.builder().dueDate(LocalDate.now().minusDays(1)).build();
        assertTrue(task.isOverdue());
        assertEquals(0, task.getDaysRemaining());
        
        task.markAsCriticalAlert();
        assertEquals(TaskPriority.CRITICAL, task.getPriorityLevel());
    }

    @Test
    void isOverdue_nullDueDate_returnsFalse() {
        PlanningTask task = PlanningTask.builder().build();
        assertFalse(task.isOverdue());
    }

    @Test
    void getDaysRemaining_nullDueDate_returnsZero() {
        PlanningTask task = PlanningTask.builder().build();
        assertEquals(0, task.getDaysRemaining());
    }

    @Test
    void getDaysRemaining_futureDueDate_returnsPositive() {
        PlanningTask task = PlanningTask.builder().dueDate(LocalDate.now().plusDays(5)).build();
        assertTrue(task.getDaysRemaining() > 0);
    }

    @Test
    void isOverdue_futureDueDate_returnsFalse() {
        PlanningTask task = PlanningTask.builder().dueDate(LocalDate.now().plusDays(3)).build();
        assertFalse(task.isOverdue());
    }

    @Test
    void getSubjectRiskLevel_nullCuts_returnsLow() {
        PlanningTask task = PlanningTask.builder().build();
        assertEquals(RiskLevel.LOW, task.getSubjectRiskLevel());
    }

    @Test
    void getSubjectRiskLevel_emptyCuts_returnsLow() {
        PlanningTask task = PlanningTask.builder().evaluationCuts(List.of()).build();
        assertEquals(RiskLevel.LOW, task.getSubjectRiskLevel());
    }

    @Test
    void getSubjectRiskLevel_withCuts_calculatesRisk() {
        // Student has one cut graded 2.0 (weight 50%) and one remaining (weight 50%)
        // Required = (3.0 - 2.0*0.5) / 0.5 = 4.0 → HIGH risk
        List<NoteRiskCalculator.CutProgress> cuts = List.of(
                new NoteRiskCalculator.CutProgress(2.0, 0.5),
                new NoteRiskCalculator.CutProgress(null, 0.5)
        );
        PlanningTask task = PlanningTask.builder().evaluationCuts(cuts).build();
        assertEquals(RiskLevel.HIGH, task.getSubjectRiskLevel());
    }

    @Test
    void getRequiredGradeForRemainingCuts_nullCuts_returnsZero() {
        PlanningTask task = PlanningTask.builder().build();
        assertEquals(0.0, task.getRequiredGradeForRemainingCuts());
    }

    @Test
    void getRequiredGradeForRemainingCuts_emptyCuts_returnsZero() {
        PlanningTask task = PlanningTask.builder().evaluationCuts(List.of()).build();
        assertEquals(0.0, task.getRequiredGradeForRemainingCuts());
    }

    @Test
    void getRequiredGradeForRemainingCuts_withCuts_calculatesGrade() {
        List<NoteRiskCalculator.CutProgress> cuts = List.of(
                new NoteRiskCalculator.CutProgress(2.0, 0.5),
                new NoteRiskCalculator.CutProgress(null, 0.5)
        );
        PlanningTask task = PlanningTask.builder().evaluationCuts(cuts).build();
        assertTrue(task.getRequiredGradeForRemainingCuts() > 0.0);
    }

    @Test
    void isHighPriority_nullLevel_returnsFalse() {
        PlanningTask task = PlanningTask.builder().build();
        assertFalse(task.isHighPriority());
    }

    @Test
    void isHighPriority_criticalLevel_returnsTrue() {
        PlanningTask task = PlanningTask.builder().priorityLevel(TaskPriority.CRITICAL).build();
        assertTrue(task.isHighPriority());
    }

    @Test
    void isHighPriority_highLevel_returnsTrue() {
        PlanningTask task = PlanningTask.builder().priorityLevel(TaskPriority.HIGH).build();
        assertTrue(task.isHighPriority());
    }

    @Test
    void isHighPriority_mediumLevel_returnsFalse() {
        PlanningTask task = PlanningTask.builder().priorityLevel(TaskPriority.MEDIUM).build();
        assertFalse(task.isHighPriority());
    }

    @Test
    void isHighPriority_lowLevel_returnsFalse() {
        PlanningTask task = PlanningTask.builder().priorityLevel(TaskPriority.LOW).build();
        assertFalse(task.isHighPriority());
    }

    @Test
    void markAsCriticalAlert_boostsPriorityScore() {
        PlanningTask task = PlanningTask.builder().priorityScore(50.0).build();
        task.markAsCriticalAlert();
        assertEquals(TaskPriority.CRITICAL, task.getPriorityLevel());
        assertEquals(99.0, task.getPriorityScore());
    }

    @Test
    void markAsCriticalAlert_keepHigherScore() {
        PlanningTask task = PlanningTask.builder().priorityScore(100.0).build();
        task.markAsCriticalAlert();
        assertEquals(TaskPriority.CRITICAL, task.getPriorityLevel());
        assertEquals(100.0, task.getPriorityScore());
    }
}

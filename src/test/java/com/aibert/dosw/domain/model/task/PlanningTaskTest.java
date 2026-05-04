package com.aibert.dosw.domain.model.task;

import com.aibert.dosw.domain.valueobjects.PriorityScore;
import java.time.LocalDate;
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
}

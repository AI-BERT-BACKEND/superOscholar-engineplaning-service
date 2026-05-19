package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.BlockType;
import com.aibert.dosw.domain.model.schedule.TimeSlot;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistributionPlan;
import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.model.task.TaskPriority;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.domain.ports.out.TaskProviderPort;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistributeTasksUseCaseImplTest {

        @Mock
        private TaskProviderPort taskProviderPort;

        @Mock
        private ScheduleProviderPort scheduleProviderPort;

        @InjectMocks
        private DistributeTasksUseCaseImpl useCase;

        @BeforeEach
        void setUp() {
                // Lenient: some tests early-return before getDailyMaxMinutes is called
                lenient().when(scheduleProviderPort.getDailyMaxMinutes(anyString())).thenReturn(240);
        }

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

        @Test
        void shouldReturnEmptyPlanWhenNoTasks() {
                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(null);
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertEquals("st1", result.getStudentId());
                assertTrue(result.getAssignedBlocks().isEmpty());
        }

        @Test
        void shouldReturnEmptyPlanWhenEmptyTasks() {
                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of());
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertTrue(result.getAssignedBlocks().isEmpty());
        }

        @Test
        void shouldReturnEmptyPlanWhenNoSchedule() {
                PlanningTask t1 = PlanningTask.builder().id("1").estimatedHours(2.0).build();
                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(null);

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertTrue(result.getAssignedBlocks().isEmpty());
                assertEquals(1, result.getUnassignedTasks().size());
        }

        @Test
        void shouldSkipDayAfterDeadline() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(2.0).dueDate(LocalDate.now())
                                .priorityLevel(TaskPriority.HIGH).build();

                DailySchedule futureDay = DailySchedule.builder()
                                .date(LocalDate.now().plusDays(5))
                                .availableSlots(List.of(TimeSlot.builder()
                                                .startTime(LocalTime.of(9, 0))
                                                .endTime(LocalTime.of(17, 0))
                                                .build()))
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(futureDay));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                // Task couldn't be assigned because all days are after the deadline
                assertEquals(1, result.getUnassignedTasks().size());
                assertTrue(result.getAssignedBlocks().isEmpty());
        }

        @Test
        void shouldDistributeWithUnavailableBlocks() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(1.0).priorityLevel(TaskPriority.HIGH).build();

                LocalDate today = LocalDate.now();
                DailySchedule day = DailySchedule.builder()
                                .date(today)
                                .availableSlots(List.of(TimeSlot.builder()
                                                .startTime(LocalTime.of(9, 0))
                                                .endTime(LocalTime.of(17, 0))
                                                .build()))
                                .build();

                UnavailableBlock block = UnavailableBlock.builder()
                                .date(today)
                                .startTime(LocalTime.of(12, 0))
                                .endTime(LocalTime.of(13, 0))
                                .reason("Lunch")
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of(block));

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertFalse(result.getAssignedBlocks().isEmpty());
        }

        @Test
        void shouldSplitTaskAcrossMultipleSlots() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(3.0).priorityLevel(TaskPriority.HIGH).build();

                LocalDate today = LocalDate.now();
                DailySchedule day = DailySchedule.builder()
                                .date(today)
                                .availableSlots(List.of(
                                                TimeSlot.builder().startTime(LocalTime.of(9, 0))
                                                                .endTime(LocalTime.of(10, 0)).build(),
                                                TimeSlot.builder().startTime(LocalTime.of(14, 0))
                                                                .endTime(LocalTime.of(16, 0)).build()))
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                // Task should be split across two slots: 1h + 2h = 3h
                assertEquals(2, result.getAssignedBlocks().size());
                assertTrue(result.getUnassignedTasks().isEmpty());
        }

        @Test
        void shouldMarkTaskAsUnassignedWhenNotEnoughTime() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(10.0).priorityLevel(TaskPriority.HIGH).build();

                DailySchedule day = DailySchedule.builder()
                                .date(LocalDate.now())
                                .availableSlots(List.of(TimeSlot.builder()
                                                .startTime(LocalTime.of(14, 0))
                                                .endTime(LocalTime.of(16, 0))
                                                .build()))
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                // Task partially assigned but still unassigned because not fully accommodated
                assertEquals(1, result.getUnassignedTasks().size());
        }

        @Test
        void shouldPrioritizeHighPriorityTasks() {
                PlanningTask high = PlanningTask.builder()
                                .id("1").estimatedHours(2.0)
                                .priorityLevel(TaskPriority.CRITICAL).priorityScore(90.0).build();
                PlanningTask low = PlanningTask.builder()
                                .id("2").estimatedHours(2.0)
                                .priorityLevel(TaskPriority.LOW).priorityScore(10.0).build();

                DailySchedule day = DailySchedule.builder()
                                .date(LocalDate.now())
                                .availableSlots(List.of(TimeSlot.builder()
                                                .startTime(LocalTime.of(9, 0))
                                                .endTime(LocalTime.of(11, 0))
                                                .build()))
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(low, high));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                // HIGH priority task should be assigned first
                assertEquals("1", result.getAssignedBlocks().get(0).getTask().getId());
                // LOW priority task should be unassigned (not enough time)
                assertEquals(1, result.getUnassignedTasks().size());
        }

        @Test
        void shouldHandleNullAvailableSlots() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(2.0).build();

                DailySchedule day = DailySchedule.builder()
                                .date(LocalDate.now())
                                .availableSlots(null)
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertEquals(1, result.getUnassignedTasks().size());
        }

        // ── AIB-25: Protección del tiempo personal ─────────────────────────────────

        @Test
        void shouldExcludeProtectedBlocksFromAssignment() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(1.0).priorityLevel(TaskPriority.HIGH).build();

                LocalDate today = LocalDate.now();
                DailySchedule day = DailySchedule.builder()
                                .date(today)
                                .availableSlots(List.of(
                                                TimeSlot.builder().startTime(LocalTime.of(9, 0))
                                                                .endTime(LocalTime.of(11, 0)).build(),
                                                TimeSlot.builder().startTime(LocalTime.of(14, 0))
                                                                .endTime(LocalTime.of(16, 0)).build()))
                                .build();

                // PERSONAL block covers 14:00-16:00 — must not receive tasks (AIB-25 RN-01)
                UnavailableBlock personalBlock = UnavailableBlock.builder()
                                .date(today)
                                .startTime(LocalTime.of(14, 0))
                                .endTime(LocalTime.of(16, 0))
                                .blockType(BlockType.PERSONAL)
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of(personalBlock));

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertFalse(result.getAssignedBlocks().isEmpty());
                result.getAssignedBlocks()
                                .forEach(block -> assertTrue(block.getStartTime().isBefore(LocalTime.of(14, 0)),
                                                "No debe asignarse en el bloque PERSONAL (14:00-16:00)"));
        }

        @Test
        void shouldReturnNoAcademicBlocksMessageWhenAllProtected() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(2.0).priorityLevel(TaskPriority.HIGH).build();

                LocalDate today = LocalDate.now();
                DailySchedule day = DailySchedule.builder()
                                .date(today)
                                .availableSlots(List.of(
                                                TimeSlot.builder().startTime(LocalTime.of(9, 0))
                                                                .endTime(LocalTime.of(17, 0)).build()))
                                .build();

                // PERSONAL block covers the entire available window (AIB-25 FA-01)
                UnavailableBlock personalBlock = UnavailableBlock.builder()
                                .date(today)
                                .startTime(LocalTime.of(9, 0))
                                .endTime(LocalTime.of(17, 0))
                                .blockType(BlockType.PERSONAL)
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of(personalBlock));

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertTrue(result.getAssignedBlocks().isEmpty());
                assertEquals(1, result.getUnassignedTasks().size());
                assertEquals(
                                "No hay bloques académicos disponibles. Revisa tu configuración de disponibilidad.",
                                result.getMessage());
        }

        @Test
        void shouldReturnConfigureAvailabilityMessageWhenNoSchedule() {
                PlanningTask t1 = PlanningTask.builder().id("1").estimatedHours(2.0).build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertEquals(
                                "Configura tu disponibilidad horaria para activar la distribución automática.",
                                result.getMessage());
                assertEquals(1, result.getUnassignedTasks().size());
        }

        // ── AIB-27: Protección de sobrecarga académica ─────────────────────────────

        @Test
        void shouldDetectOverloadedDayWhenTasksExceedDailyLimit() {
                // Two tasks totalling 300 min — over MAX_MINUTES_PER_DAY (240)
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(2.5).priorityLevel(TaskPriority.HIGH).priorityScore(90.0)
                                .build();
                PlanningTask t2 = PlanningTask.builder()
                                .id("2").estimatedHours(2.5).priorityLevel(TaskPriority.HIGH).priorityScore(80.0)
                                .build();

                LocalDate today = LocalDate.now();
                // Slot of 7 hours — plenty of raw time, but cap is 4 h (240 min)
                DailySchedule day = DailySchedule.builder()
                                .date(today)
                                .availableSlots(List.of(TimeSlot.builder()
                                                .startTime(LocalTime.of(8, 0))
                                                .endTime(LocalTime.of(15, 0))
                                                .build()))
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1, t2));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                // Uncapped proposed total = 300 min > 240 → one overloaded day
                assertEquals(1, result.getOverloadedDays().size());
                assertEquals(today, result.getOverloadedDays().get(0).getDate());
                assertEquals(60, result.getOverloadedDays().get(0).getExcessMinutes()); // 300 - 240
        }

        @Test
        void shouldReturnEmptyOverloadedDaysWhenWithinDailyLimit() {
                // Single task of 120 min — well within 240-min cap
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(2.0).priorityLevel(TaskPriority.HIGH).build();

                DailySchedule day = DailySchedule.builder()
                                .date(LocalDate.now())
                                .availableSlots(List.of(TimeSlot.builder()
                                                .startTime(LocalTime.of(9, 0))
                                                .endTime(LocalTime.of(12, 0))
                                                .build()))
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertTrue(result.getOverloadedDays().isEmpty());
                assertEquals("Tu plan está dentro de tu límite diario.", result.getMessage());
        }

        @Test
        void shouldReturnAdjustedMessageWhenOverloadDetected() {
                PlanningTask t1 = PlanningTask.builder()
                                .id("1").estimatedHours(2.5).priorityLevel(TaskPriority.CRITICAL).priorityScore(95.0)
                                .build();
                PlanningTask t2 = PlanningTask.builder()
                                .id("2").estimatedHours(2.5).priorityLevel(TaskPriority.HIGH).priorityScore(75.0)
                                .build();

                DailySchedule day = DailySchedule.builder()
                                .date(LocalDate.now())
                                .availableSlots(List.of(TimeSlot.builder()
                                                .startTime(LocalTime.of(8, 0))
                                                .endTime(LocalTime.of(15, 0))
                                                .build()))
                                .build();

                when(taskProviderPort.getPendingTasksByUser("st1")).thenReturn(List.of(t1, t2));
                when(scheduleProviderPort.getWeeklySchedule("st1")).thenReturn(List.of(day));
                when(scheduleProviderPort.getUnavailableBlocks("st1")).thenReturn(List.of());

                WeeklyDistributionPlan result = useCase.distribute("st1");

                assertEquals("Tu plan fue ajustado para respetar tu límite diario de estudio.", result.getMessage());
        }
}

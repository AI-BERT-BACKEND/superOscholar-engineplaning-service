package com.aibert.dosw.application.event;

import com.aibert.dosw.domain.ports.in.PrioritizeTasksUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Asynchronous listener for task change events (AIB-22.1).
 * Triggers automatic priority recalculation in the background whenever
 * a NUEVA_TAREA, EDICION, or COMPLETADO event is received,
 * without interrupting the student's navigation.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TaskChangeEventListener {

    private final PrioritizeTasksUseCase prioritizeTasksUseCase;

    /**
     * Handles a task change event by running automatic prioritization
     * asynchronously on the planning thread pool.
     *
     * @param event the task change event containing studentId and event type
     */
    @Async("planningTaskExecutor")
    @EventListener
    public void onTaskChange(TaskChangeEvent event) {
        log.info("AIB-22.1: Evento '{}' recibido para el estudiante '{}' (taskId='{}')."
                + " Iniciando recálculo automático de prioridades.",
                event.getEventType(), sl(event.getStudentId()), sl(event.getTaskId()));

        try {
            prioritizeTasksUseCase.prioritize(event.getStudentId(), true);
            log.info("AIB-22.1: Recálculo automático completado para el estudiante '{}'.",
                    sl(event.getStudentId()));
        } catch (Exception ex) {
            log.error("AIB-22.1: Error durante el recálculo automático para el estudiante '{}': {}",
                    sl(event.getStudentId()), ex.getMessage(), ex);
        }
    }

    private static String sl(String s) {
        return s == null ? "" : s.replaceAll("[\r\n]", "_");
    }
}

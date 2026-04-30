package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Output port for planning task persistence.
 * The infrastructure implements this contract.
 */
public interface PlanningTaskRepositoryPort {

    List<PlanningTask> findPendingByUserId(String userId);

    List<PlanningTask> findCriticalByUserId(String userId);

    List<PlanningTask> findByUserIdAndScheduledDate(
            String userId,
            LocalDate date);

    List<PlanningTask> findByUserIdAndDueDateBetween(
            String userId,
            LocalDate startDate,
            LocalDate endDate);

    List<PlanningTask> findOverdueByUserId(String userId);

    Optional<PlanningTask> findById(String taskId);

    PlanningTask save(PlanningTask task);

    List<PlanningTask> saveAll(List<PlanningTask> tasks);
}

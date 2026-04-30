package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.PlanningTaskEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for planning tasks.
 */
public interface SpringDataPlanningTaskRepository
        extends JpaRepository<PlanningTaskEntity, String> {

    /**
     * Pending or in-progress tasks for a user.
     */
    @Query("""
            SELECT t FROM PlanningTaskEntity t
            WHERE t.userId = :userId
              AND t.status IN ('PENDING', 'SCHEDULED', 'IN_PROGRESS', 'OVERLOADED')
            ORDER BY t.priorityScore DESC
            """)
    List<PlanningTaskEntity> findPendingByUserId(
            @Param("userId") String userId);

    /**
     * Critical tasks (score >= 75) for a user.
     */
    @Query("""
            SELECT t FROM PlanningTaskEntity t
            WHERE t.userId = :userId
              AND t.priorityScore >= 75
              AND t.status NOT IN ('COMPLETED')
            ORDER BY t.priorityScore DESC
            """)
    List<PlanningTaskEntity> findCriticalByUserId(
            @Param("userId") String userId);

    /**
     * Tasks scheduled for a specific date.
     */
    List<PlanningTaskEntity> findByUserIdAndScheduledDate(
            String userId,
            LocalDate scheduledDate);

    /**
     * Tasks with deadline in a date range.
     */
    @Query("""
            SELECT t FROM PlanningTaskEntity t
            WHERE t.userId = :userId
              AND t.dueDate BETWEEN :startDate AND :endDate
              AND t.status NOT IN ('COMPLETED')
            ORDER BY t.dueDate ASC
            """)
    List<PlanningTaskEntity> findByUserIdAndDueDateBetween(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Overdue tasks that are not completed.
     */
    @Query("""
            SELECT t FROM PlanningTaskEntity t
            WHERE t.userId = :userId
              AND t.dueDate < :today
              AND t.status NOT IN ('COMPLETED')
            """)
    List<PlanningTaskEntity> findOverdueByUserId(
            @Param("userId") String userId,
            @Param("today") LocalDate today);
}

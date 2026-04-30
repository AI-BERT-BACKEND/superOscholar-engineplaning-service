package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * JPA entity for planning task persistence.
 * Maps the planning_tasks table in PostgreSQL.
 */
@Entity
@Table(name = "planning_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanningTaskEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_hours", nullable = false)
    private double estimatedHours;

    @Column(name = "difficulty", nullable = false)
    private int difficulty;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;

    @Column(name = "subject_credits", nullable = false)
    private int subjectCredits;

    @Column(name = "task_weight_in_grade", nullable = false)
    private double taskWeightInGrade;

    @Column(name = "grade_period1")
    private Double gradePeriod1;

    @Column(name = "grade_period2")
    private Double gradePeriod2;

    @Column(name = "weight_period1", nullable = false)
    private double weightPeriod1;

    @Column(name = "weight_period2", nullable = false)
    private double weightPeriod2;

    @Column(name = "weight_period3", nullable = false)
    private double weightPeriod3;

    @Column(name = "priority_score")
    private double priorityScore;

    @Column(name = "priority_level", length = 20)
    private String priorityLevel;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

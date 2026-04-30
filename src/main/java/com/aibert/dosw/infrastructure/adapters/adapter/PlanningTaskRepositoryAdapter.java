package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.task.PlanningTask;
import com.aibert.dosw.domain.ports.out.PlanningTaskRepositoryPort;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.PlanningTaskEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.PlanningTaskEntityMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.SpringDataPlanningTaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Implementation of PlanningTaskRepositoryPort.
 * Connects the domain with Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class PlanningTaskRepositoryAdapter
        implements PlanningTaskRepositoryPort {

    private final SpringDataPlanningTaskRepository repository;
    private final PlanningTaskEntityMapper mapper;

    @Override
    public List<PlanningTask> findPendingByUserId(String userId) {
        return repository.findPendingByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanningTask> findCriticalByUserId(String userId) {
        return repository.findCriticalByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanningTask> findByUserIdAndScheduledDate(
            String userId, LocalDate date) {
        return repository.findByUserIdAndScheduledDate(userId, date)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanningTask> findByUserIdAndDueDateBetween(
            String userId,
            LocalDate startDate,
            LocalDate endDate) {
        return repository.findByUserIdAndDueDateBetween(
                userId,
                startDate,
                endDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PlanningTask> findById(String taskId) {
        return repository.findById(taskId)
                .map(mapper::toDomain);
    }

    @Override
    public PlanningTask save(PlanningTask task) {
        PlanningTaskEntity entity = mapper.toEntity(task);
        PlanningTaskEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<PlanningTask> saveAll(List<PlanningTask> tasks) {
        List<PlanningTaskEntity> entities = tasks.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        return repository.saveAll(entities)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanningTask> findOverdueByUserId(String userId) {
        return repository.findOverdueByUserId(userId, LocalDate.now())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}

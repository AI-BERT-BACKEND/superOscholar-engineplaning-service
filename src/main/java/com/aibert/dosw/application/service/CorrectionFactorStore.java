package com.aibert.dosw.application.service;

import com.aibert.dosw.domain.model.task.TaskType;
import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * In-memory store for the per-student, per-task-type correction factor history
 * (AIB-22.4).
 *
 * <p>
 * Holds the last {@link #MAX_HISTORY} actual/estimated ratios for each
 * {@code (studentId, taskType)} pair. Thread-safe per key via synchronized
 * blocks on each individual queue.
 * </p>
 */
@Slf4j
@Component
public class CorrectionFactorStore {

    static final int MAX_HISTORY = 10;

    /**
     * Key: "studentId:taskType" → ordered queue of the last N ratios (oldest
     * first).
     */
    private final ConcurrentHashMap<String, ArrayDeque<Double>> ratioHistory = new ConcurrentHashMap<>();

    /**
     * Records a new actual/estimated ratio for the given student and task type.
     * Keeps at most {@link #MAX_HISTORY} entries, dropping the oldest when full.
     *
     * @param studentId student identifier
     * @param taskType  task type used to segment the factor
     * @param ratio     actualTime / estimatedDurationMinutes for the completed task
     */
    public void record(String studentId, TaskType taskType, double ratio) {
        String key = buildKey(studentId, taskType);
        ArrayDeque<Double> queue = ratioHistory.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (queue) {
            queue.addLast(ratio);
            while (queue.size() > MAX_HISTORY) {
                queue.pollFirst();
            }
        }
        log.debug("AIB-22.4: Ratio {:.3f} registrado para '{}'/{}. Historial: {} entrada(s).",
                ratio, studentId, taskType, getCount(studentId, taskType));
    }

    /**
     * Returns an immutable snapshot of the stored ratios for the given key,
     * ordered from oldest to newest. Returns an empty list when no history exists.
     *
     * @param studentId student identifier
     * @param taskType  task type
     * @return unmodifiable list of stored ratios
     */
    public List<Double> getRecentRatios(String studentId, TaskType taskType) {
        String key = buildKey(studentId, taskType);
        ArrayDeque<Double> queue = ratioHistory.getOrDefault(key, new ArrayDeque<>());
        synchronized (queue) {
            return List.copyOf(queue);
        }
    }

    /**
     * Returns how many ratio samples are currently stored for this student/type
     * pair.
     *
     * @param studentId student identifier
     * @param taskType  task type
     * @return sample count
     */
    public int getCount(String studentId, TaskType taskType) {
        return getRecentRatios(studentId, taskType).size();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String buildKey(String studentId, TaskType taskType) {
        return studentId + ":" + taskType.name();
    }
}

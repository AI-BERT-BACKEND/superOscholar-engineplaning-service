package com.aibert.dosw.application.service;

import com.aibert.dosw.domain.model.task.TaskType;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorrectionFactorStoreTest {

    private final CorrectionFactorStore store = new CorrectionFactorStore();

    @Test
    void shouldReturnEmptyListWhenNoHistory() {
        List<Double> ratios = store.getRecentRatios("stu-1", TaskType.TAREA);
        assertNotNull(ratios);
        assertTrue(ratios.isEmpty());
    }

    @Test
    void shouldReturnZeroCountWhenNoHistory() {
        assertEquals(0, store.getCount("stu-1", TaskType.TAREA));
    }

    @Test
    void shouldRecordAndReturnRatios() {
        store.record("stu-1", TaskType.TAREA, 1.2);
        store.record("stu-1", TaskType.TAREA, 0.8);

        List<Double> ratios = store.getRecentRatios("stu-1", TaskType.TAREA);
        assertEquals(2, ratios.size());
        assertEquals(1.2, ratios.get(0));
        assertEquals(0.8, ratios.get(1));
    }

    @Test
    void shouldSegmentByTaskType() {
        store.record("stu-1", TaskType.TAREA, 1.0);
        store.record("stu-1", TaskType.EXAMEN, 2.0);

        assertEquals(1, store.getCount("stu-1", TaskType.TAREA));
        assertEquals(1, store.getCount("stu-1", TaskType.EXAMEN));
        assertEquals(1.0, store.getRecentRatios("stu-1", TaskType.TAREA).get(0));
        assertEquals(2.0, store.getRecentRatios("stu-1", TaskType.EXAMEN).get(0));
    }

    @Test
    void shouldSegmentByStudentId() {
        store.record("stu-1", TaskType.TAREA, 1.5);
        store.record("stu-2", TaskType.TAREA, 0.5);

        assertEquals(1, store.getCount("stu-1", TaskType.TAREA));
        assertEquals(1, store.getCount("stu-2", TaskType.TAREA));
    }

    @Test
    void shouldEvictOldestWhenMaxHistoryExceeded() {
        // Record MAX_HISTORY + 1 entries
        for (int i = 1; i <= CorrectionFactorStore.MAX_HISTORY + 1; i++) {
            store.record("stu-evict", TaskType.PROYECTO, (double) i);
        }

        List<Double> ratios = store.getRecentRatios("stu-evict", TaskType.PROYECTO);
        assertEquals(CorrectionFactorStore.MAX_HISTORY, ratios.size());
        // Oldest (1.0) should have been dropped; first remaining should be 2.0
        assertEquals(2.0, ratios.get(0));
    }

    @Test
    void shouldReturnImmutableSnapshot() {
        store.record("stu-1", TaskType.LECTURA, 1.1);
        List<Double> snapshot = store.getRecentRatios("stu-1", TaskType.LECTURA);

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(9.9));
    }

    @Test
    void shouldCountCorrectly() {
        store.record("stu-count", TaskType.OTRO, 1.0);
        store.record("stu-count", TaskType.OTRO, 1.1);
        store.record("stu-count", TaskType.OTRO, 1.2);

        assertEquals(3, store.getCount("stu-count", TaskType.OTRO));
    }
}

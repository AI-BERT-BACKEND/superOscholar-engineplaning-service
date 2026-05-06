package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileServiceClientFallbackTest {

    private final ProfileServiceClientFallback fallback = new ProfileServiceClientFallback();

    @Test
    void getWeeklySchedule_returnsEmptyList() {
        List<DailySchedule> result = fallback.getWeeklySchedule("student1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUnavailableBlocks_returnsEmptyList() {
        List<UnavailableBlock> result = fallback.getUnavailableBlocks("student1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

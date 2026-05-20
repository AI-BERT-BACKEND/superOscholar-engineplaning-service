package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import com.aibert.dosw.infrastructure.external.feign.client.ProfileServiceClient;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceAdapterTest {

    @Mock
    private ProfileServiceClient profileServiceClient;

    @InjectMocks
    private ScheduleServiceAdapter adapter;

    @Test
    void shouldGetWeeklySchedule() {
        DailySchedule schedule = DailySchedule.builder()
                .date(LocalDate.now()).totalAvailableHours(5.0).build();
        when(profileServiceClient.getWeeklySchedule("st1")).thenReturn(List.of(schedule));

        List<DailySchedule> result = adapter.getWeeklySchedule("st1");

        assertEquals(1, result.size());
        verify(profileServiceClient).getWeeklySchedule("st1");
    }

    @Test
    void shouldGetUnavailableBlocks() {
        UnavailableBlock block = UnavailableBlock.builder()
                .date(LocalDate.now()).reason("class").build();
        when(profileServiceClient.getUnavailableBlocks("st1")).thenReturn(List.of(block));

        List<UnavailableBlock> result = adapter.getUnavailableBlocks("st1");

        assertEquals(1, result.size());
        verify(profileServiceClient).getUnavailableBlocks("st1");
    }

    @Test
    void shouldGetDailyMaxMinutes() {
        when(profileServiceClient.getDailyMaxMinutes("st1")).thenReturn(180);

        int result = adapter.getDailyMaxMinutes("st1");

        assertEquals(180, result);
        verify(profileServiceClient).getDailyMaxMinutes("st1");
    }
}

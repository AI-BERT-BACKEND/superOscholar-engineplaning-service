package com.aibert.dosw.infrastructure.external.feign.adapter;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import com.aibert.dosw.domain.ports.out.ScheduleProviderPort;
import com.aibert.dosw.infrastructure.external.feign.client.ProfileServiceClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Secondary adapter that implements the ScheduleProviderPort using Feign.
 */
@Component
@RequiredArgsConstructor
public class ScheduleServiceAdapter implements ScheduleProviderPort {

    private final ProfileServiceClient profileServiceClient;

    @Override
    public List<DailySchedule> getWeeklySchedule(String studentId) {
        return profileServiceClient.getWeeklySchedule(studentId);
    }

    @Override
    public List<UnavailableBlock> getUnavailableBlocks(String studentId) {
        return profileServiceClient.getUnavailableBlocks(studentId);
    }

    @Override
    public int getDailyMaxMinutes(String studentId) {
        return profileServiceClient.getDailyMaxMinutes(studentId);
    }
}

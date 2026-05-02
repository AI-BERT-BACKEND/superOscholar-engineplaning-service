package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign Client to communicate with the profile-service.
 */
@FeignClient(name = "profile-service", url = "${feign.profile-service.url}")
public interface ProfileServiceClient {

    @GetMapping("/schedule/weekly")
    List<DailySchedule> getWeeklySchedule(@RequestParam("studentId") String studentId);

    @GetMapping("/schedule/unavailable")
    List<UnavailableBlock> getUnavailableBlocks(@RequestParam("studentId") String studentId);
}

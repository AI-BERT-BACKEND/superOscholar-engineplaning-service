package com.aibert.dosw.misc;

import com.aibert.dosw.application.dto.response.DayBalanceResponse;
import com.aibert.dosw.application.dto.response.RebalanceResultResponse;
import com.aibert.dosw.application.mapper.ScheduleMapper;
import com.aibert.dosw.domain.model.context.RebalanceResult;
import com.aibert.dosw.domain.model.context.UserProfile;
import com.aibert.dosw.domain.model.schedule.AvailabilityBlock;
import com.aibert.dosw.domain.model.schedule.WeeklyDistribution;
import com.aibert.dosw.domain.valueobjects.AcademicWeight;
import com.aibert.dosw.domain.valueobjects.UserId;
import com.aibert.dosw.entrypoints.rest.mapper.PlanningResponseMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmptyClassesInstantiationTest {

    @Test
    void shouldInstantiateEmptyClasses() {
        assertNotNull(new UserId());
        assertNotNull(new AcademicWeight());
        assertNotNull(new UserProfile());
        assertNotNull(new RebalanceResult());
        assertNotNull(new WeeklyDistribution());
        assertNotNull(new AvailabilityBlock());
        assertNotNull(new ScheduleMapper());
        assertNotNull(new PlanningResponseMapper());
        assertNotNull(DayBalanceResponse.builder().build());
        assertNotNull(new RebalanceResultResponse());
    }
}

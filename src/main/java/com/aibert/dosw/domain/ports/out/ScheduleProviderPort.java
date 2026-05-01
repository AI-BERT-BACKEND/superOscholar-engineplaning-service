package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import java.util.List;

/**
 * Output port to retrieve the student's availability schedule.
 * Abstracts the communication with the profile-service.
 */
public interface ScheduleProviderPort {
    
    /**
     * Retrieves the daily schedules (availability) for a student for the current week.
     *
     * @param studentId The ID of the student
     * @return List of daily schedules containing available hours
     */
    List<DailySchedule> getWeeklySchedule(String studentId);
}

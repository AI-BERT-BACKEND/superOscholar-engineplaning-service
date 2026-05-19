package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.schedule.DailySchedule;
import com.aibert.dosw.domain.model.schedule.UnavailableBlock;
import java.util.List;

/**
 * Output port to retrieve the student's availability schedule.
 * Abstracts the communication with the profile-service.
 */
public interface ScheduleProviderPort {

    /**
     * Retrieves the daily schedules (availability) for a student for the current
     * week.
     *
     * @param studentId The ID of the student
     * @return List of daily schedules containing available hours
     */
    List<DailySchedule> getWeeklySchedule(String studentId);

    /**
     * Retrieves the time blocks explicitly marked as unavailable by the student.
     *
     * @param studentId The ID of the student
     * @return List of unavailable blocks
     */
    List<UnavailableBlock> getUnavailableBlocks(String studentId);

    /**
     * Retrieves the student's configured daily study limit in minutes (AIB-27
     * RN-03).
     * Returns 240 by default when not explicitly configured.
     *
     * @param studentId The ID of the student
     * @return Daily study limit in minutes
     */
    int getDailyMaxMinutes(String studentId);
}

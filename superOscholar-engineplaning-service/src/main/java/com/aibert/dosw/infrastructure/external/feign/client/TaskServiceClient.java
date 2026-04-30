package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.infrastructure.external.feign.dto.ExternalTaskDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client to consume the task-service.
 * Retrieves student tasks from the task management service (Module 3).
 */
@FeignClient(name = "task-service", url = "${services.task-service.url}")
public interface TaskServiceClient {

    /**
     * Retrieves all pending tasks for a student.
     *
     * @param userId student id
     * @return list of pending tasks
     */
    @GetMapping("/api/v1/tasks/user/{userId}/pending")
    List<ExternalTaskDto> getPendingTasksByUser(
            @PathVariable("userId") String userId);

    /**
     * Retrieves tasks for a student by scheduled date.
     *
     * @param userId student id
     * @param date   date in yyyy-MM-dd format
     * @return list of tasks for that date
     */
    @GetMapping("/api/v1/tasks/user/{userId}/by-date")
    List<ExternalTaskDto> getTasksByDate(
            @PathVariable("userId") String userId,
            @RequestParam("date") String date);
}

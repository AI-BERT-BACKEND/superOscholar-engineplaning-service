package com.aibert.dosw.infrastructure.external.feign.client;

import com.aibert.dosw.domain.model.task.PlanningTask;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign Client to communicate with the task-service.
 */
@FeignClient(name = "task-service", url = "${feign.task-service.url}")
public interface TaskServiceClient {

    @GetMapping("/tasks/pending")
    List<PlanningTask> getPendingTasks(@RequestParam("studentId") String studentId);

    @GetMapping("/tasks/scheduled")
    List<PlanningTask> getScheduledTasks(@RequestParam("studentId") String studentId);

    @PutMapping("/tasks/priorities")
    void updateTaskPriorities(@RequestBody List<PlanningTask> tasks);

    @PostMapping("/tasks/failure")
    void reportTaskFailure(
            @RequestParam("studentId") String studentId, 
            @RequestParam("taskId") String taskId, 
            @RequestParam("hoursMissed") double hoursMissed, 
            @RequestParam("reason") String reason);
}

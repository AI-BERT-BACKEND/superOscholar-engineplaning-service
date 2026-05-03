package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.FailureReportRequest;
import com.aibert.dosw.application.dto.response.DistributionPlanResponse;
import com.aibert.dosw.application.mapper.PlanningTaskMapper;
import com.aibert.dosw.domain.ports.in.RebalanceTasksUseCase;
import com.aibert.dosw.entrypoints.rest.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for dynamic rebalancing scenarios (R17).
 */
@RestController
@RequestMapping("/planning/rebalance")
@RequiredArgsConstructor
public class RebalanceController {

    private final RebalanceTasksUseCase rebalanceTasksUseCase;
    private final PlanningTaskMapper planningTaskMapper;

    /**
     * Endpoint for the frontend to report that a study block failed or was missed.
     * Automatically triggers a rebalance and returns the new schedule.
     */
    @PostMapping("/failure")
    public ResponseEntity<ApiResponse<DistributionPlanResponse>> reportFailure(
            @RequestBody FailureReportRequest request) {

        var newPlan = rebalanceTasksUseCase.reportFailureAndRebalance(
                request.getStudentId(),
                request.getTaskId(),
                request.getFailedDate(),
                request.getHoursMissed(),
                request.getReason()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Failure reported and schedule rebalanced successfully",
                        planningTaskMapper.toDistributionPlanResponse(newPlan))
        );
    }

    /**
     * Endpoint to explicitly reorganize the remaining pending tasks 
     * into the remaining time of the week.
     */
    @PostMapping("/reorganize")
    public ResponseEntity<ApiResponse<DistributionPlanResponse>> reorganize(
            @RequestParam String studentId) {

        var updatedPlan = rebalanceTasksUseCase.reorganizePlan(studentId);

        return ResponseEntity.ok(
                ApiResponse.success("Weekly schedule reorganized successfully",
                        planningTaskMapper.toDistributionPlanResponse(updatedPlan))
        );
    }
}

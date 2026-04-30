package com.aibert.dosw.entrypoints.rest.mapper;

import com.aibert.dosw.application.dto.request.BalanceRequest;
import com.aibert.dosw.application.dto.request.DistributeTasksRequest;
import com.aibert.dosw.application.dto.request.PrioritizeRequest;
import com.aibert.dosw.application.dto.request.RebalanceRequest;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Entrypoint mapper that converts HTTP parameters
 * into application layer request DTOs.
 *
 * Input: @PathVariable, @RequestParam, @RequestBody
 * Output: application/dto/request/*
 */
@Component
public class PlanningRequestMapper {

        /**
         * Builds a PrioritizeRequest from HTTP parameters.
         */
        public PrioritizeRequest toPrioritizeRequest(
                        String userId,
                        int totalCredits,
                        boolean forceRecalculate) {

                return PrioritizeRequest.builder()
                                .userId(userId)
                                .totalCredits(totalCredits)
                                .forceRecalculate(forceRecalculate)
                                .build();
        }

        /**
         * Builds a BalanceRequest from HTTP parameters.
         */
        public BalanceRequest toBalanceRequest(
                        String userId,
                        LocalDate weekStart) {

                return BalanceRequest.builder()
                                .userId(userId)
                                .weekStart(weekStart != null ? weekStart : LocalDate.now())
                                .build();
        }

        /**
         * Builds a DistributeTasksRequest from HTTP parameters.
         */
        public DistributeTasksRequest toDistributeRequest(
                        String userId,
                        int totalCredits,
                        boolean respectPersonalTime) {

                return DistributeTasksRequest.builder()
                                .userId(userId)
                                .totalCredits(totalCredits)
                                .respectPersonalTime(respectPersonalTime)
                                .build();
        }

        /**
         * Enriches a RebalanceRequest with the path userId.
         */
        public RebalanceRequest toRebalanceRequest(
                        String userId,
                        RebalanceRequest body) {

                return RebalanceRequest.builder()
                                .userId(userId)
                                .trigger(body.getTrigger())
                                .build();
        }

}

package com.aibert.dosw.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configurable weights for the priority calculation formula (R14).
 * Values can be changed at runtime via application.yml or environment variables
 * without modifying code.
 *
 * <p>
 * The three weights must sum to 1.0 for the formula to produce a score in
 * 0–100.
 * </p>
 *
 * <pre>
 * planning:
 *   priority:
 *     weight-proximity: 0.40
 *     weight-academic: 0.40
 *     weight-time: 0.20
 * </pre>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "planning.priority")
public class PriorityWeightsProperties {

    /** Weight for deadline proximity factor (default 40%). */
    private double weightProximity = 0.40;

    /** Weight for academic/subject weight factor (default 40%). */
    private double weightAcademic = 0.40;

    /** Weight for estimated time factor (default 20%). */
    private double weightTime = 0.20;

    /** Correction factor for estimated duration minutes (AIB-22.4). */
    private double timeCorrectionFactor = 1.0;
}

package com.aibert.dosw.infrastructure.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Async configuration for background task processing (AIB-22.1).
 * Provides a dedicated thread pool used by the automatic prioritization
 * listener
 * so that recalculations never block the main request thread.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Thread pool executor dedicated to planning background operations.
     * Core size 2, max 5, queue 100 — tuned for low-frequency prioritization
     * events.
     */
    @Bean("planningTaskExecutor")
    public Executor planningTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("planning-async-");
        executor.initialize();
        return executor;
    }
}

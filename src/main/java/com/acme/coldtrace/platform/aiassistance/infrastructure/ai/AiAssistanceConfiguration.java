package com.acme.coldtrace.platform.aiassistance.infrastructure.ai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Infrastructure configuration for AI assistance adapters.
 *
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(AiAssistanceProperties.class)
public class AiAssistanceConfiguration {
    /**
     * Executor used to bound synchronous provider calls with a timeout.
     *
     * @return executor service for AI provider calls
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService aiAssistanceExecutorService() {
        return Executors.newCachedThreadPool();
    }
}

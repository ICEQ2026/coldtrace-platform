package com.acme.coldtrace.platform.iam.infrastructure.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * IAM bounded context configuration.
 *
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(PasswordResetEmailProperties.class)
public class IamConfiguration {
}

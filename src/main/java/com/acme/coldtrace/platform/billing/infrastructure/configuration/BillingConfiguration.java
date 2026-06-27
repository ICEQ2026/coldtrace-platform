package com.acme.coldtrace.platform.billing.infrastructure.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Billing bounded context configuration.
 *
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(BillingPlanCatalogProperties.class)
public class BillingConfiguration {
}

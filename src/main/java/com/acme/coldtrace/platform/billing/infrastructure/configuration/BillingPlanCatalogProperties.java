package com.acme.coldtrace.platform.billing.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Environment-driven billing plan catalog configuration.
 *
 * @param operationsStripePriceId optional Stripe price id for Operations
 * @param complianceAiStripePriceId optional Stripe price id for Compliance AI
 * @since 1.0
 */
@ConfigurationProperties(prefix = "coldtrace.billing.plan-catalog")
public record BillingPlanCatalogProperties(
        String operationsStripePriceId,
        String complianceAiStripePriceId
) {
    /**
     * Normalizes optional provider identifiers.
     */
    public BillingPlanCatalogProperties {
        operationsStripePriceId = normalizeOptionalText(operationsStripePriceId);
        complianceAiStripePriceId = normalizeOptionalText(complianceAiStripePriceId);
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

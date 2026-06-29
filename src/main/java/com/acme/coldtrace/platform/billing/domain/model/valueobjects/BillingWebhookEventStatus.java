package com.acme.coldtrace.platform.billing.domain.model.valueobjects;

/**
 * Processing status for a signed billing provider webhook event.
 *
 * @since 1.0
 */
public enum BillingWebhookEventStatus {
    PROCESSED,
    IGNORED
}

package com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook;

/**
 * Provider-level webhook verification and parsing failures.
 *
 * @since 1.0
 */
public enum BillingWebhookProviderFailure {
    NOT_CONFIGURED,
    MISSING_SIGNATURE,
    INVALID_SIGNATURE,
    INVALID_PAYLOAD
}

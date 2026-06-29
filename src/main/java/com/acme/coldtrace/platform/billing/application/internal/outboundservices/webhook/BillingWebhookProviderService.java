package com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook;

import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Outbound provider service for verifying and normalizing signed billing webhooks.
 *
 * @since 1.0
 */
public interface BillingWebhookProviderService {
    /**
     * Verifies the provider signature and returns a normalized event.
     *
     * @param payload raw request body
     * @param signatureHeader signature header sent by the provider
     * @return normalized event or controlled provider failure
     */
    Result<BillingWebhookProviderEvent, BillingWebhookProviderFailure> parseSignedEvent(
            String payload,
            String signatureHeader
    );
}

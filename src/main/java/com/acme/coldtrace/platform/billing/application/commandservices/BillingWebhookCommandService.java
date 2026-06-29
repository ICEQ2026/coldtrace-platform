package com.acme.coldtrace.platform.billing.application.commandservices;

import com.acme.coldtrace.platform.billing.application.model.BillingWebhookProcessingResult;
import com.acme.coldtrace.platform.billing.domain.model.commands.ProcessStripeWebhookCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for billing provider webhooks.
 *
 * @since 1.0
 */
public interface BillingWebhookCommandService {
    /**
     * Processes a signed Stripe webhook request.
     *
     * @param command webhook processing command
     * @return processing result or controlled failure
     */
    Result<BillingWebhookProcessingResult, BillingWebhookCommandFailure> handle(ProcessStripeWebhookCommand command);
}

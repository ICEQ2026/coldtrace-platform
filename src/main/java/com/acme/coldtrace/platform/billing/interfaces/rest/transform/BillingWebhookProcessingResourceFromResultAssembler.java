package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.application.model.BillingWebhookProcessingResult;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.BillingWebhookProcessingResource;

/**
 * Converts billing webhook application results into REST resources.
 *
 * @since 1.0
 */
public final class BillingWebhookProcessingResourceFromResultAssembler {
    private BillingWebhookProcessingResourceFromResultAssembler() {
    }

    /**
     * Converts the application result into a response resource.
     *
     * @param result webhook processing result
     * @return REST response resource
     */
    public static BillingWebhookProcessingResource toResourceFromResult(BillingWebhookProcessingResult result) {
        return new BillingWebhookProcessingResource(
                result.provider(),
                result.eventId(),
                result.eventType(),
                result.processingStatus(),
                result.duplicate(),
                result.organizationId(),
                result.planCode(),
                result.subscriptionStatus()
        );
    }
}

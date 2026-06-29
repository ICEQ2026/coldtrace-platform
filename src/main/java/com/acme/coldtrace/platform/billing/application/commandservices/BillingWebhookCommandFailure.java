package com.acme.coldtrace.platform.billing.application.commandservices;

/**
 * Failure types for billing webhook processing.
 *
 * @since 1.0
 */
public sealed interface BillingWebhookCommandFailure
        permits BillingWebhookCommandFailure.ProviderNotConfigured,
        BillingWebhookCommandFailure.MissingSignature,
        BillingWebhookCommandFailure.InvalidSignature,
        BillingWebhookCommandFailure.InvalidPayload,
        BillingWebhookCommandFailure.ProcessingFailed {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** Stripe webhook signing secret is not configured. */
    record ProviderNotConfigured() implements BillingWebhookCommandFailure {
        @Override
        public String messageKey() {
            return "billing.webhook.error.provider-not-configured";
        }
    }

    /** Stripe-Signature header is missing. */
    record MissingSignature() implements BillingWebhookCommandFailure {
        @Override
        public String messageKey() {
            return "billing.webhook.error.signature-missing";
        }
    }

    /** Stripe signature verification failed. */
    record InvalidSignature() implements BillingWebhookCommandFailure {
        @Override
        public String messageKey() {
            return "billing.webhook.error.signature-invalid";
        }
    }

    /** Payload could not be parsed into a provider event. */
    record InvalidPayload() implements BillingWebhookCommandFailure {
        @Override
        public String messageKey() {
            return "billing.webhook.error.payload-invalid";
        }
    }

    /** Internal processing failed after verification. */
    record ProcessingFailed() implements BillingWebhookCommandFailure {
        @Override
        public String messageKey() {
            return "billing.webhook.error.processing-failed";
        }
    }
}

package com.acme.coldtrace.platform.billing.application.commandservices;

/**
 * Failure types for billing customer portal session creation.
 *
 * @since 1.0
 */
public sealed interface BillingPortalSessionCommandFailure
        permits BillingPortalSessionCommandFailure.OrganizationNotFound,
        BillingPortalSessionCommandFailure.OrganizationSubscriptionNotFound,
        BillingPortalSessionCommandFailure.ProviderCustomerNotFound,
        BillingPortalSessionCommandFailure.ProviderNotConfigured,
        BillingPortalSessionCommandFailure.ProviderUnavailable {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** Organization not found failure. */
    record OrganizationNotFound() implements BillingPortalSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.portal-session.error.organization-not-found";
        }
    }

    /** Organization subscription not found failure. */
    record OrganizationSubscriptionNotFound() implements BillingPortalSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.portal-session.error.organization-subscription-not-found";
        }
    }

    /** Organization has no provider customer identifier yet. */
    record ProviderCustomerNotFound() implements BillingPortalSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.portal-session.error.provider-customer-not-found";
        }
    }

    /** Stripe secret key or return URL are missing. */
    record ProviderNotConfigured() implements BillingPortalSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.portal-session.error.provider-not-configured";
        }
    }

    /** Provider call failed. */
    record ProviderUnavailable() implements BillingPortalSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.portal-session.error.provider-unavailable";
        }
    }
}

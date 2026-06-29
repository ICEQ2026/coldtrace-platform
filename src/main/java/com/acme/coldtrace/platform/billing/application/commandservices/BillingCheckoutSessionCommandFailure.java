package com.acme.coldtrace.platform.billing.application.commandservices;

/**
 * Failure types for billing checkout session creation.
 *
 * @since 1.0
 */
public sealed interface BillingCheckoutSessionCommandFailure
        permits BillingCheckoutSessionCommandFailure.OrganizationNotFound,
        BillingCheckoutSessionCommandFailure.OrganizationSubscriptionNotFound,
        BillingCheckoutSessionCommandFailure.TargetPlanNotFound,
        BillingCheckoutSessionCommandFailure.FreePlanCheckoutNotAllowed,
        BillingCheckoutSessionCommandFailure.PlanProviderPriceNotConfigured,
        BillingCheckoutSessionCommandFailure.ProviderNotConfigured,
        BillingCheckoutSessionCommandFailure.ProviderUnavailable {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements BillingCheckoutSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.checkout-session.error.organization-not-found";
        }
    }

    /** Organization subscription not found failure. */
    record OrganizationSubscriptionNotFound() implements BillingCheckoutSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.checkout-session.error.organization-subscription-not-found";
        }
    }

    /** Target subscription plan not found failure. */
    record TargetPlanNotFound(String planCode) implements BillingCheckoutSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.checkout-session.error.target-plan-not-found";
        }

        @Override
        public Object[] args() {
            return new Object[]{planCode};
        }
    }

    /** Free plan cannot be routed through Stripe Checkout. */
    record FreePlanCheckoutNotAllowed(String planCode) implements BillingCheckoutSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.checkout-session.error.free-plan-not-allowed";
        }

        @Override
        public Object[] args() {
            return new Object[]{planCode};
        }
    }

    /** Paid plan has no provider price id configured. */
    record PlanProviderPriceNotConfigured(String planCode) implements BillingCheckoutSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.checkout-session.error.plan-provider-price-not-configured";
        }

        @Override
        public Object[] args() {
            return new Object[]{planCode};
        }
    }

    /** Stripe secret key or redirect URLs are missing. */
    record ProviderNotConfigured() implements BillingCheckoutSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.checkout-session.error.provider-not-configured";
        }
    }

    /** Provider call failed. */
    record ProviderUnavailable() implements BillingCheckoutSessionCommandFailure {
        @Override
        public String messageKey() {
            return "billing.checkout-session.error.provider-unavailable";
        }
    }
}

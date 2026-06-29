package com.acme.coldtrace.platform.billing.application.queryservices;

/**
 * Failure types for organization subscription queries.
 *
 * @since 1.0
 */
public sealed interface OrganizationSubscriptionQueryFailure
        permits OrganizationSubscriptionQueryFailure.OrganizationNotFound,
        OrganizationSubscriptionQueryFailure.OrganizationSubscriptionNotFound,
        OrganizationSubscriptionQueryFailure.SubscriptionPlanNotFound {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements OrganizationSubscriptionQueryFailure {
        @Override
        public String messageKey() {
            return "billing.organization-subscription.error.organization-not-found";
        }
    }

    /** Organization subscription not found failure. */
    record OrganizationSubscriptionNotFound() implements OrganizationSubscriptionQueryFailure {
        @Override
        public String messageKey() {
            return "billing.organization-subscription.error.organization-subscription-not-found";
        }
    }

    /** Subscription plan referenced by the subscription was not found. */
    record SubscriptionPlanNotFound(String planCode) implements OrganizationSubscriptionQueryFailure {
        @Override
        public String messageKey() {
            return "billing.organization-subscription.error.subscription-plan-not-found";
        }

        @Override
        public Object[] args() {
            return new Object[] { planCode };
        }
    }
}

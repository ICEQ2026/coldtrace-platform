package com.acme.coldtrace.platform.billing.domain.model.valueobjects;

/**
 * Lifecycle status for an organization's current subscription.
 *
 * @since 1.0
 */
public enum SubscriptionStatus {
    FREE,
    ACTIVE,
    PAST_DUE,
    CANCELED;

    /**
     * @return true when the subscription can unlock its plan limits and features
     */
    public boolean allowsPlanEntitlements() {
        return this == FREE || this == ACTIVE;
    }
}

package com.acme.coldtrace.platform.billing.domain.model.queries;

/**
 * Query for retrieving an organization's active subscription details.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetOrganizationSubscriptionByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates the organization identifier.
     */
    public GetOrganizationSubscriptionByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("billing.organization-subscription.error.organizationId.invalid");
        }
    }
}

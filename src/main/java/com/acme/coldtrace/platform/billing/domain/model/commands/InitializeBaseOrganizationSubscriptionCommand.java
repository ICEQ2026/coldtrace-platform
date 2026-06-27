package com.acme.coldtrace.platform.billing.domain.model.commands;

/**
 * Command that ensures one organization has a Base subscription record.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record InitializeBaseOrganizationSubscriptionCommand(Long organizationId) {
    /**
     * Validates the organization identifier.
     */
    public InitializeBaseOrganizationSubscriptionCommand {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("billing.organization-subscription.error.organizationId.invalid");
        }
    }
}

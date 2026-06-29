package com.acme.coldtrace.platform.billing.domain.model.commands;

/**
 * Command to create a provider-hosted customer portal session.
 *
 * @param organizationId organization requesting billing portal access
 * @since 1.0
 */
public record CreateBillingPortalSessionCommand(Long organizationId) {
}

package com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal;

/**
 * Request sent to the external customer portal provider adapter.
 *
 * @param organizationId organization requesting portal access
 * @param providerCustomerId provider customer identifier
 * @since 1.0
 */
public record PortalSessionProviderRequest(
        Long organizationId,
        String providerCustomerId
) {
}

package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving gateways by organization.
 *
 * @param organizationId organization identifier
 * @since 1.0
 */
public record GetGatewaysByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates the organization identifier.
     *
     * @throws IllegalArgumentException if the organization identifier is invalid
     */
    public GetGatewaysByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.gateway.error.organizationId.invalid");
        }
    }
}

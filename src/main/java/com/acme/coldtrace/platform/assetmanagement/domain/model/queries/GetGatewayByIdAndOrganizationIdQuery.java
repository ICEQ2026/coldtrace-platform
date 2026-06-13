package com.acme.coldtrace.platform.assetmanagement.domain.model.queries;

/**
 * Query for retrieving one gateway by id and organization.
 *
 * @param organizationId organization identifier
 * @param gatewayId gateway identifier
 * @since 1.0
 */
public record GetGatewayByIdAndOrganizationIdQuery(Long organizationId, Long gatewayId) {
    /**
     * Validates gateway query identifiers.
     *
     * @throws IllegalArgumentException if identifiers are invalid
     */
    public GetGatewayByIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("asset-management.gateway.error.organizationId.invalid");
        }
        if (gatewayId == null || gatewayId <= 0) {
            throw new IllegalArgumentException("asset-management.gateway.error.gatewayId.invalid");
        }
    }
}

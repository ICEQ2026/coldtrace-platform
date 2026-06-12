package com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries;

/**
 * Query used to list technical service requests for one organization.
 * <p>
 * Listing is intentionally scoped by organization because maintenance operations
 * belong to a business tenant. The application service uses this value to check that
 * the organization exists before consulting maintenance persistence.
 *
 * @param organizationId identifier of the organization that owns the requests
 * @since 1.0
 */
public record GetTechnicalServiceRequestsByOrganizationIdQuery(Long organizationId) {
    /**
     * Validates that the organization identifier can be used as a route-scoped key.
     *
     * @throws IllegalArgumentException when the organization identifier is null or not positive
     */
    public GetTechnicalServiceRequestsByOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("organizationId must be positive");
        }
    }
}

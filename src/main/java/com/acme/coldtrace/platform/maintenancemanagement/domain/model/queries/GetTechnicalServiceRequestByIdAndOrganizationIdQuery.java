package com.acme.coldtrace.platform.maintenancemanagement.domain.model.queries;

/**
 * Query used to retrieve one technical service request that belongs to an organization.
 * <p>
 * The organization identifier is part of the query because technical service requests
 * are exposed through organization-scoped REST routes. Keeping the organization in the
 * query makes the application service verify tenant ownership before returning data.
 *
 * @param organizationId identifier of the organization that owns the request
 * @param technicalServiceRequestId identifier of the technical service request to retrieve
 * @since 1.0
 */
public record GetTechnicalServiceRequestByIdAndOrganizationIdQuery(
        Long organizationId,
        Long technicalServiceRequestId
) {
    /**
     * Validates the identifiers required by the query.
     *
     * @throws IllegalArgumentException when any identifier is null or not positive
     */
    public GetTechnicalServiceRequestByIdAndOrganizationIdQuery {
        if (organizationId == null || organizationId <= 0) {
            throw new IllegalArgumentException("maintenance-management.technical-service-request.error.organizationId.invalid");
        }
        if (technicalServiceRequestId == null || technicalServiceRequestId <= 0) {
            throw new IllegalArgumentException("maintenance-management.technical-service-request.error.requestId.invalid");
        }
    }
}

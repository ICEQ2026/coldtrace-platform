package com.acme.coldtrace.platform.identityaccess.application.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllOrganizationsQuery;

import java.util.List;

/**
 * Application service contract providing read access to organizations.
 *
 * @since 1.0
 */
public interface OrganizationQueryService {
    /**
     * Retrieves all organizations.
     *
     * @param query query object representing the all-organizations request
     * @return list of organizations, possibly empty
     * @see GetAllOrganizationsQuery
     */
    List<Organization> handle(GetAllOrganizationsQuery query);
}

package com.acme.coldtrace.platform.assetmanagement.application.queryservices;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Location;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetLocationByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetLocationsByOrganizationIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract providing read access to locations.
 *
 * @since 1.0
 */
public interface LocationQueryService {
    /**
     * Retrieves locations by organization.
     *
     * @param query query object containing the organization identifier
     * @return locations for the organization, possibly empty
     * @see GetLocationsByOrganizationIdQuery
     */
    List<Location> handle(GetLocationsByOrganizationIdQuery query);

    /**
     * Retrieves one location by id and organization.
     *
     * @param query query object containing organization and location identifiers
     * @return location when found, otherwise empty
     * @see GetLocationByIdAndOrganizationIdQuery
     */
    Optional<Location> handle(GetLocationByIdAndOrganizationIdQuery query);
}

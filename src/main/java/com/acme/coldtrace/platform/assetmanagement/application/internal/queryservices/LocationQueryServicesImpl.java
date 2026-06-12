package com.acme.coldtrace.platform.assetmanagement.application.internal.queryservices;

import com.acme.coldtrace.platform.assetmanagement.application.queryservices.LocationQueryService;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Location;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetLocationByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetLocationsByOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementation for location query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class LocationQueryServicesImpl implements LocationQueryService {
    private final LocationRepository locationRepository;

    public LocationQueryServicesImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Retrieves locations from persistence by organization.
     *
     * @param query query object containing the organization identifier
     * @return locations for the organization, possibly empty
     * @see GetLocationsByOrganizationIdQuery
     */
    @Override
    public List<Location> handle(GetLocationsByOrganizationIdQuery query) {
        log.debug("Querying locations by organizationId={}", query.organizationId());
        var locations = locationRepository.findAllByOrganizationId(query.organizationId());
        log.debug("Found {} locations for organizationId={}", locations.size(), query.organizationId());
        return locations;
    }

    /**
     * Retrieves one location from persistence by id and organization.
     *
     * @param query query object containing organization and location identifiers
     * @return location when found, otherwise empty
     * @see GetLocationByIdAndOrganizationIdQuery
     */
    @Override
    public Optional<Location> handle(GetLocationByIdAndOrganizationIdQuery query) {
        log.debug("Querying location by organizationId={}, locationId={}",
                query.organizationId(), query.locationId());
        var location = locationRepository.findByIdAndOrganizationId(query.locationId(), query.organizationId());
        if (location.isEmpty()) {
            log.warn("Location not found: organizationId={}, locationId={}",
                    query.organizationId(), query.locationId());
        }
        return location;
    }
}

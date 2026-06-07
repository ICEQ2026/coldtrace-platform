package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for querying and persisting {@link Location} aggregates.
 *
 * @since 1.0
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    /**
     * Finds all locations that belong to an organization.
     *
     * @param organizationId organization identifier
     * @return locations for the organization
     */
    List<Location> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one location by id and organization.
     *
     * @param id location identifier
     * @param organizationId organization identifier
     * @return location when found
     */
    Optional<Location> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Checks whether a location name already exists in the organization.
     *
     * @param organizationId organization identifier
     * @param name location name
     * @return true when a location with the provided name exists
     */
    boolean existsByOrganizationIdAndNameIgnoreCase(Long organizationId, String name);

    /**
     * Checks whether a location name is used by another location in the organization.
     *
     * @param organizationId organization identifier
     * @param name location name
     * @param id location identifier to exclude
     * @return true when another location with the provided name exists
     */
    boolean existsByOrganizationIdAndNameIgnoreCaseAndIdNot(Long organizationId, String name, Long id);
}

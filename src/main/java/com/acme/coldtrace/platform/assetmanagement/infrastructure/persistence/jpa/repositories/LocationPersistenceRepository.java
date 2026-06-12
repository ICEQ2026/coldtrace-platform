package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.LocationName;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.LocationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for location persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface LocationPersistenceRepository extends JpaRepository<LocationPersistenceEntity, Long> {
    /**
     * Finds locations by organization.
     *
     * @param organizationId organization identifier
     * @return persistence entities for the organization
     */
    List<LocationPersistenceEntity> findAllByOrganizationId(Long organizationId);

    /**
     * Finds one location by id and organization.
     *
     * @param id location identifier
     * @param organizationId organization identifier
     * @return persistence entity when found
     */
    Optional<LocationPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);

    /**
     * Checks whether a location name already exists in an organization.
     *
     * @param organizationId organization identifier
     * @param name location name value object
     * @return true when the name exists
     */
    boolean existsByOrganizationIdAndName(Long organizationId, LocationName name);

    /**
     * Checks whether another location uses a location name.
     *
     * @param organizationId organization identifier
     * @param name location name value object
     * @param id location identifier excluded from the search
     * @return true when another entity uses the name
     */
    boolean existsByOrganizationIdAndNameAndIdNot(Long organizationId, LocationName name, Long id);
}

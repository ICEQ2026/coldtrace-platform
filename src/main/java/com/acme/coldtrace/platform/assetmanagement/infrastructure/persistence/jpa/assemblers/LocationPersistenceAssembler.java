package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Location;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.LocationPersistenceEntity;

/**
 * Assembler that translates locations between domain and persistence models.
 *
 * @since 1.0
 */
public final class LocationPersistenceAssembler {
    private LocationPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity read from the database
     * @return location aggregate rebuilt from persisted state
     */
    public static Location toDomainFromPersistence(LocationPersistenceEntity entity) {
        return new Location(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getName(),
                entity.getType(),
                entity.getAddress(),
                entity.getDescription(),
                entity.getStatus()
        );
    }

    /**
     * Creates a persistence entity from a domain aggregate.
     *
     * @param location location aggregate to persist
     * @return persistence entity with copied domain state
     */
    public static LocationPersistenceEntity toPersistenceFromDomain(Location location) {
        var entity = new LocationPersistenceEntity();
        entity.setId(location.getId());
        copyDomainState(location, entity);
        return entity;
    }

    /**
     * Copies mutable location state into an existing persistence entity.
     *
     * @param location source domain aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(Location location, LocationPersistenceEntity entity) {
        entity.setOrganizationId(location.getOrganizationId());
        entity.setName(location.getNameValue());
        entity.setType(location.getType());
        entity.setAddress(location.getAddress());
        entity.setDescription(location.getDescription());
        entity.setStatus(location.getStatus());
    }
}

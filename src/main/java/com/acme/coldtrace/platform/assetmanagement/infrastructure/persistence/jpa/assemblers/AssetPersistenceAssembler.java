package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.AssetPersistenceEntity;

/**
 * Assembler that translates assets between domain and persistence models.
 * <p>
 * The mapping is deliberately explicit so persistence concerns cannot leak
 * into the aggregate and domain services cannot depend on JPA entity state.
 *
 * @since 1.0
 */
public final class AssetPersistenceAssembler {
    private AssetPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity read from the database
     * @return asset aggregate rebuilt from persisted state
     */
    public static Asset toDomainFromPersistence(AssetPersistenceEntity entity) {
        return new Asset(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getLocationId(),
                entity.getUuid(),
                entity.getType(),
                entity.getName(),
                entity.getCapacity(),
                entity.getDescription(),
                entity.getStatus()
        );
    }

    /**
     * Creates a new persistence entity from a domain aggregate.
     *
     * @param asset asset aggregate to persist
     * @return persistence entity with asset data copied from the aggregate
     */
    public static AssetPersistenceEntity toPersistenceFromDomain(Asset asset) {
        var entity = new AssetPersistenceEntity();
        entity.setId(asset.getId());
        copyDomainState(asset, entity);
        return entity;
    }

    /**
     * Copies mutable asset state from the aggregate into an existing entity.
     * <p>
     * Repository adapters use this method during updates to preserve auditing
     * values already managed by JPA on the existing persistence entity.
     *
     * @param asset source domain aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(Asset asset, AssetPersistenceEntity entity) {
        entity.setOrganizationId(asset.getOrganizationId());
        entity.setLocationId(asset.getLocationId());
        entity.setUuid(asset.getUuidValue());
        entity.setType(asset.getType());
        entity.setName(asset.getName());
        entity.setCapacity(asset.getCapacity());
        entity.setDescription(asset.getDescription());
        entity.setStatus(asset.getStatus());
    }
}

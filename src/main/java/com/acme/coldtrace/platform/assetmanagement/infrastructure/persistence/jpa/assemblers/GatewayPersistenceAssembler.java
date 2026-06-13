package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Gateway;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.GatewayPersistenceEntity;

/**
 * Assembler that translates gateways between domain and persistence models.
 *
 * @since 1.0
 */
public final class GatewayPersistenceAssembler {
    private GatewayPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity read from the database
     * @return gateway aggregate rebuilt from persisted state
     */
    public static Gateway toDomainFromPersistence(GatewayPersistenceEntity entity) {
        return new Gateway(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getLocationId(),
                entity.getUuid(),
                entity.getName(),
                entity.getNetwork(),
                entity.getStatus()
        );
    }

    /**
     * Creates a persistence entity from a domain aggregate.
     *
     * @param gateway gateway aggregate to persist
     * @return persistence entity with copied domain state
     */
    public static GatewayPersistenceEntity toPersistenceFromDomain(Gateway gateway) {
        var entity = new GatewayPersistenceEntity();
        entity.setId(gateway.getId());
        copyDomainState(gateway, entity);
        return entity;
    }

    /**
     * Copies mutable gateway state into an existing persistence entity.
     *
     * @param gateway source domain aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(Gateway gateway, GatewayPersistenceEntity entity) {
        entity.setOrganizationId(gateway.getOrganizationId());
        entity.setLocationId(gateway.getLocationId());
        entity.setUuid(gateway.getUuidValue());
        entity.setName(gateway.getName());
        entity.setNetwork(gateway.getNetwork());
        entity.setStatus(gateway.getStatus());
    }
}

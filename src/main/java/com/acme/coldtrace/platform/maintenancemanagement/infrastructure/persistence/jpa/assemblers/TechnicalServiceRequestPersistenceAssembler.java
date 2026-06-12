package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.entities.TechnicalServiceRequestPersistenceEntity;

/**
 * Assembler that translates technical service requests between domain and persistence models.
 *
 * @since 1.0
 */
public final class TechnicalServiceRequestPersistenceAssembler {
    private TechnicalServiceRequestPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity read from the database
     * @return technical service request aggregate rebuilt from persisted state
     */
    public static TechnicalServiceRequest toDomainFromPersistence(TechnicalServiceRequestPersistenceEntity entity) {
        return new TechnicalServiceRequest(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getCode(),
                entity.getAssetId(),
                entity.getAssetLocationId(),
                entity.getAssetName(),
                entity.getIncidentId(),
                entity.getIssueDescription(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getRequestedBy(),
                entity.getRequestedAt(),
                entity.getClosedAt(),
                entity.getClosureSummary(),
                entity.getEvidence(),
                entity.getClosedBy()
        );
    }

    /**
     * Creates a persistence entity from a domain aggregate.
     *
     * @param request technical service request aggregate to persist
     * @return persistence entity with copied domain state
     */
    public static TechnicalServiceRequestPersistenceEntity toPersistenceFromDomain(TechnicalServiceRequest request) {
        var entity = new TechnicalServiceRequestPersistenceEntity();
        copyDomainState(request, entity);
        return entity;
    }

    /**
     * Copies mutable domain state into an existing managed JPA entity.
     *
     * @param request source aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(TechnicalServiceRequest request, TechnicalServiceRequestPersistenceEntity entity) {
        entity.setId(request.getId());
        entity.setOrganizationId(request.getOrganizationId());
        entity.setCode(request.getCode());
        entity.setAssetId(request.getAssetId());
        entity.setAssetLocationId(request.getAssetLocationId());
        entity.setAssetName(request.getAssetName());
        entity.setIncidentId(request.getIncidentId());
        entity.setIssueDescription(request.getIssueDescription());
        entity.setPriority(request.getPriority());
        entity.setStatus(request.getStatus());
        entity.setRequestedBy(request.getRequestedBy());
        entity.setRequestedAt(request.getRequestedAt());
        entity.setClosedAt(request.getClosedAt());
        entity.setClosureSummary(request.getClosureSummary());
        entity.setEvidence(request.getEvidence());
        entity.setClosedBy(request.getClosedBy());
    }
}

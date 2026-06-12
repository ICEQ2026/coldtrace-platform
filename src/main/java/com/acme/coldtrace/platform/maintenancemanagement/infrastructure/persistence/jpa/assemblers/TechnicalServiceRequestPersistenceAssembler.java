package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.entities.TechnicalServiceRequestPersistenceEntity;

public final class TechnicalServiceRequestPersistenceAssembler {
    private TechnicalServiceRequestPersistenceAssembler() {
    }

    public static TechnicalServiceRequest toDomainFromPersistence(TechnicalServiceRequestPersistenceEntity entity) {
        return new TechnicalServiceRequest(entity.getId(), entity.getOrganizationId(), entity.getCode(),
                entity.getAssetId(), entity.getAssetLocationId(), entity.getAssetName(), entity.getIncidentId(),
                entity.getIssueDescription(), entity.getPriority(), entity.getStatus(), entity.getRequestedBy(),
                entity.getRequestedAt(), entity.getClosedAt(), entity.getClosureSummary(), entity.getEvidence(),
                entity.getClosedBy());
    }

    public static TechnicalServiceRequestPersistenceEntity toPersistenceFromDomain(TechnicalServiceRequest request) {
        var entity = new TechnicalServiceRequestPersistenceEntity();
        copyDomainState(request, entity);
        return entity;
    }

    public static void copyDomainState(TechnicalServiceRequest request, TechnicalServiceRequestPersistenceEntity entity) {
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

package com.acme.coldtrace.platform.reports.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.infrastructure.persistence.jpa.entities.ReportPersistenceEntity;

/**
 * Assembler that translates reports between domain and persistence models.
 *
 * @since 1.0
 */
public final class ReportPersistenceAssembler {
    private ReportPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into a domain aggregate.
     *
     * @param entity persistence entity read from the database
     * @return report aggregate rebuilt from persisted state
     */
    public static Report toDomainFromPersistence(ReportPersistenceEntity entity) {
        return new Report(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getUuid(),
                entity.getType(),
                entity.getTitle(),
                entity.getPeriodStart(),
                entity.getPeriodEnd(),
                entity.getGeneratedAt(),
                entity.getAssetCount(),
                entity.getReadingCount(),
                entity.getOutOfRangeReadingCount(),
                entity.getIncidentCount(),
                entity.getOpenIncidentCount(),
                entity.getAverageTemperature(),
                entity.getAverageHumidity(),
                entity.getCompliancePercentage()
        );
    }

    /**
     * Creates a persistence entity from a domain aggregate.
     *
     * @param report report aggregate to persist
     * @return persistence entity with copied domain state
     */
    public static ReportPersistenceEntity toPersistenceFromDomain(Report report) {
        var entity = new ReportPersistenceEntity();
        entity.setId(report.getId());
        entity.setOrganizationId(report.getOrganizationId());
        entity.setUuid(report.getUuid());
        entity.setType(report.getType());
        entity.setTitle(report.getTitle());
        entity.setPeriodStart(report.getPeriodStart());
        entity.setPeriodEnd(report.getPeriodEnd());
        entity.setGeneratedAt(report.getGeneratedAt());
        entity.setAssetCount(report.getAssetCount());
        entity.setReadingCount(report.getReadingCount());
        entity.setOutOfRangeReadingCount(report.getOutOfRangeReadingCount());
        entity.setIncidentCount(report.getIncidentCount());
        entity.setOpenIncidentCount(report.getOpenIncidentCount());
        entity.setAverageTemperature(report.getAverageTemperature());
        entity.setAverageHumidity(report.getAverageHumidity());
        entity.setCompliancePercentage(report.getCompliancePercentage());
        return entity;
    }
}

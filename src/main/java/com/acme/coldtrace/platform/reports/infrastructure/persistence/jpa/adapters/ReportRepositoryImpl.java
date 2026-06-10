package com.acme.coldtrace.platform.reports.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.domain.repositories.ReportRepository;
import com.acme.coldtrace.platform.reports.infrastructure.persistence.jpa.assemblers.ReportPersistenceAssembler;
import com.acme.coldtrace.platform.reports.infrastructure.persistence.jpa.repositories.ReportPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed adapter for the report domain repository.
 *
 * @since 1.0
 */
@Repository
public class ReportRepositoryImpl implements ReportRepository {
    private final ReportPersistenceRepository reportPersistenceRepository;

    public ReportRepositoryImpl(ReportPersistenceRepository reportPersistenceRepository) {
        this.reportPersistenceRepository = reportPersistenceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Report> findAllByOrganizationId(Long organizationId) {
        return reportPersistenceRepository.findAllByOrganizationIdOrderByGeneratedAtDesc(organizationId).stream()
                .map(ReportPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Report> findByIdAndOrganizationId(Long id, Long organizationId) {
        return reportPersistenceRepository.findByIdAndOrganizationId(id, organizationId)
                .map(ReportPersistenceAssembler::toDomainFromPersistence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report save(Report report) {
        var entity = ReportPersistenceAssembler.toPersistenceFromDomain(report);
        var savedEntity = reportPersistenceRepository.save(entity);
        return ReportPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}

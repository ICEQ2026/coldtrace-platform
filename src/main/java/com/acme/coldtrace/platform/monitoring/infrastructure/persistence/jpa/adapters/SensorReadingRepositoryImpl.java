package com.acme.coldtrace.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.acme.coldtrace.platform.monitoring.infrastructure.persistence.jpa.assemblers.SensorReadingPersistenceAssembler;
import com.acme.coldtrace.platform.monitoring.infrastructure.persistence.jpa.repositories.SensorReadingPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed adapter for the sensor reading domain repository.
 *
 * @since 1.0
 */
@Repository
public class SensorReadingRepositoryImpl implements SensorReadingRepository {
    private final SensorReadingPersistenceRepository sensorReadingPersistenceRepository;

    public SensorReadingRepositoryImpl(SensorReadingPersistenceRepository sensorReadingPersistenceRepository) {
        this.sensorReadingPersistenceRepository = sensorReadingPersistenceRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SensorReading> findAllByOrganizationId(Long organizationId) {
        return sensorReadingPersistenceRepository.findAllByOrganizationIdOrderByRecordedAtDesc(organizationId).stream()
                .map(SensorReadingPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SensorReading> findByIdAndOrganizationId(Long id, Long organizationId) {
        return sensorReadingPersistenceRepository.findByIdAndOrganizationId(id, organizationId)
                .map(SensorReadingPersistenceAssembler::toDomainFromPersistence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SensorReading save(SensorReading sensorReading) {
        var entity = SensorReadingPersistenceAssembler.toPersistenceFromDomain(sensorReading);
        var savedEntity = sensorReadingPersistenceRepository.save(entity);
        return SensorReadingPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}

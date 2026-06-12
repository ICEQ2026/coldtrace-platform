package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.adapters;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.domain.repositories.MaintenanceScheduleRepository;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.assemblers.MaintenanceSchedulePersistenceAssembler;
import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.repositories.MaintenanceSchedulePersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed adapter for the maintenance schedule domain repository.
 * <p>
 * The adapter keeps persistence entities behind the repository boundary and
 * publishes schedule-created domain events only after the database assigns the
 * identifier required by downstream integration listeners.
 *
 * @since 1.0
 */
@Repository
public class MaintenanceScheduleRepositoryImpl implements MaintenanceScheduleRepository {
    private final MaintenanceSchedulePersistenceRepository maintenanceSchedulePersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MaintenanceScheduleRepositoryImpl(
            MaintenanceSchedulePersistenceRepository maintenanceSchedulePersistenceRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.maintenanceSchedulePersistenceRepository = maintenanceSchedulePersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MaintenanceSchedule> findAllByOrganizationId(Long organizationId) {
        return maintenanceSchedulePersistenceRepository.findAllByOrganizationIdOrderByScheduledDateAsc(organizationId)
                .stream()
                .map(MaintenanceSchedulePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MaintenanceSchedule> findByIdAndOrganizationId(Long id, Long organizationId) {
        return maintenanceSchedulePersistenceRepository.findByIdAndOrganizationId(id, organizationId)
                .map(MaintenanceSchedulePersistenceAssembler::toDomainFromPersistence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MaintenanceSchedule save(MaintenanceSchedule schedule) {
        if (schedule.getId() == null) {
            var entity = MaintenanceSchedulePersistenceAssembler.toPersistenceFromDomain(schedule);
            var savedSchedule = MaintenanceSchedulePersistenceAssembler.toDomainFromPersistence(
                    maintenanceSchedulePersistenceRepository.save(entity)
            );
            savedSchedule.onCreated();
            savedSchedule.domainEvents().forEach(eventPublisher::publishEvent);
            savedSchedule.clearDomainEvents();
            return savedSchedule;
        }

        var entity = maintenanceSchedulePersistenceRepository.findById(schedule.getId())
                .orElseGet(() -> MaintenanceSchedulePersistenceAssembler.toPersistenceFromDomain(schedule));
        MaintenanceSchedulePersistenceAssembler.copyDomainState(schedule, entity);
        return MaintenanceSchedulePersistenceAssembler.toDomainFromPersistence(
                maintenanceSchedulePersistenceRepository.save(entity)
        );
    }
}

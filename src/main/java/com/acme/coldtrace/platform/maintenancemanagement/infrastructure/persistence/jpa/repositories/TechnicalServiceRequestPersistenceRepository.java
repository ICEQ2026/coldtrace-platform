package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.entities.TechnicalServiceRequestPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TechnicalServiceRequestPersistenceRepository extends JpaRepository<TechnicalServiceRequestPersistenceEntity, Long> {
    List<TechnicalServiceRequestPersistenceEntity> findAllByOrganizationId(Long organizationId);
    Optional<TechnicalServiceRequestPersistenceEntity> findByIdAndOrganizationId(Long id, Long organizationId);
}

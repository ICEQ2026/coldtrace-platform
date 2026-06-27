package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities.SubscriptionPlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for subscription plan persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface SubscriptionPlanPersistenceRepository extends JpaRepository<SubscriptionPlanPersistenceEntity, Long> {
    /**
     * Finds active plans ordered by monthly price.
     *
     * @return active plan persistence entities
     */
    List<SubscriptionPlanPersistenceEntity> findAllByActiveTrueOrderByMonthlyPriceCentsAsc();

    /**
     * Finds a plan by stable code.
     *
     * @param code stable plan code
     * @return persistence entity when found
     */
    Optional<SubscriptionPlanPersistenceEntity> findByCode(String code);
}

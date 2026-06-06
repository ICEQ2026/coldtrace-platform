package com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for querying and persisting {@link Organization} aggregates.
 *
 * @since 1.0
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    /**
     * Checks whether an organization exists by contact email ignoring case.
     *
     * @param contactEmail organization contact email
     * @return true when an organization with the provided contact email exists
     */
    boolean existsByContactEmailIgnoreCase(String contactEmail);

    /**
     * Checks whether an organization exists by tax identifier ignoring case.
     *
     * @param taxId organization tax identifier
     * @return true when an organization with the provided tax identifier exists
     */
    boolean existsByTaxIdIgnoreCase(String taxId);
}

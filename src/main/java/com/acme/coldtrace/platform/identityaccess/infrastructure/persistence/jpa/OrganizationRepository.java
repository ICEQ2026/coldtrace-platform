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
}

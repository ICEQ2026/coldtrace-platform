package com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.repositories;

import com.acme.coldtrace.platform.identityaccess.domain.model.valueobjects.EmailAddress;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for user persistence entities.
 *
 * @since 1.0
 */
@Repository
public interface UserPersistenceRepository extends JpaRepository<UserPersistenceEntity, Long> {
    /**
     * Finds users by organization.
     *
     * @param organizationId organization identifier
     * @return persistence entities for the organization
     */
    List<UserPersistenceEntity> findAllByOrganizationId(Long organizationId);

    /**
     * Checks whether an email exists.
     *
     * @param email user email value object
     * @return true when the email exists
     */
    boolean existsByEmail(EmailAddress email);
}

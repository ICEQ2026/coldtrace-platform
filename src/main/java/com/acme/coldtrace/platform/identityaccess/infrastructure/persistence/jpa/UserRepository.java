package com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for querying and persisting {@link User} aggregates.
 *
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds all users associated with an organization.
     *
     * @param organizationId organization identifier used to filter users
     * @return users belonging to the organization, possibly empty
     */
    List<User> findAllByOrganizationId(Long organizationId);

    /**
     * Checks whether a user exists by email ignoring case.
     *
     * @param email user email
     * @return true when a user with the provided email exists
     */
    boolean existsByEmailIgnoreCase(String email);
}

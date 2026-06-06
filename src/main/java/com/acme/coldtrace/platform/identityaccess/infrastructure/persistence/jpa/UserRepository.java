package com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for querying and persisting {@link User} aggregates.
 *
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Checks whether a user exists by email ignoring case.
     *
     * @param email user email
     * @return true when a user with the provided email exists
     */
    boolean existsByEmailIgnoreCase(String email);
}

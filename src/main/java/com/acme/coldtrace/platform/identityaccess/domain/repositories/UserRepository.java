package com.acme.coldtrace.platform.identityaccess.domain.repositories;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;

import java.util.List;

/**
 * Domain repository contract for user aggregates.
 *
 * @since 1.0
 */
public interface UserRepository {
    /**
     * Finds users linked to an organization.
     *
     * @param organizationId organization identifier
     * @return users for the organization
     */
    List<User> findAllByOrganizationId(Long organizationId);

    /**
     * Persists a user aggregate.
     *
     * @param user user aggregate
     * @return persisted user rebuilt from persistence state
     */
    User save(User user);

    /**
     * Checks whether an email is already used by a user.
     *
     * @param email user email
     * @return true when the email exists
     */
    boolean existsByEmail(String email);
}

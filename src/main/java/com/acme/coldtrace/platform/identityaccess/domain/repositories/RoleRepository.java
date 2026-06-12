package com.acme.coldtrace.platform.identityaccess.domain.repositories;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository contract for role aggregates.
 *
 * @since 1.0
 */
public interface RoleRepository {
    /**
     * Finds all roles.
     *
     * @return available roles
     */
    List<Role> findAll();

    /**
     * Finds one role by stable name.
     *
     * @param name stable role name
     * @return role when found
     */
    Optional<Role> findByName(String name);

    /**
     * Checks whether a role exists by identifier.
     *
     * @param id role identifier
     * @return true when the role exists
     */
    boolean existsById(Long id);

    /**
     * Checks whether a role exists by stable name.
     *
     * @param name stable role name
     * @return true when the role exists
     */
    boolean existsByName(String name);

    /**
     * Persists a role aggregate.
     *
     * @param role role aggregate
     * @return persisted role rebuilt from persistence state
     */
    Role save(Role role);
}

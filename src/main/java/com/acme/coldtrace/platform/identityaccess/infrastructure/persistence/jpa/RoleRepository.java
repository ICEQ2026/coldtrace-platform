package com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for querying and persisting {@link Role} aggregates.
 *
 * @since 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Finds a role by its stable name.
     *
     * @param name role name
     * @return role when found
     */
    Optional<Role> findByName(String name);

    /**
     * Checks whether a role exists by its stable name.
     *
     * @param name role name
     * @return true when a role with the provided name exists
     */
    boolean existsByName(String name);
}

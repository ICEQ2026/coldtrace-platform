package com.acme.coldtrace.platform.identityaccess.application.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Role;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllRolesQuery;

import java.util.List;

/**
 * Application service contract providing read access to roles.
 *
 * @since 1.0
 */
public interface RoleQueryService {
    /**
     * Retrieves all roles and their permissions.
     *
     * @param query query object representing the all-roles request
     * @return list of roles, possibly empty
     * @see GetAllRolesQuery
     */
    List<Role> handle(GetAllRolesQuery query);
}

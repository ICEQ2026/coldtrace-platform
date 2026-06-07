package com.acme.coldtrace.platform.identityaccess.application.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetUsersByOrganizationIdQuery;

import java.util.List;

/**
 * Application service contract providing read access to users.
 *
 * @since 1.0
 */
public interface UserQueryService {
    /**
     * Retrieves users by organization.
     *
     * @param query query object containing the organization identifier
     * @return users for the organization, possibly empty
     * @see GetUsersByOrganizationIdQuery
     */
    List<User> handle(GetUsersByOrganizationIdQuery query);
}

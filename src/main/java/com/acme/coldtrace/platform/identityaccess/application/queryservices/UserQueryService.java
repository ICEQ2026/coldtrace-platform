package com.acme.coldtrace.platform.identityaccess.application.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllUsersQuery;

import java.util.List;

/**
 * Application service contract providing read access to users.
 *
 * @since 1.0
 */
public interface UserQueryService {
    /**
     * Retrieves all users.
     *
     * @param query query object representing the all-users request
     * @return list of users, possibly empty
     * @see GetAllUsersQuery
     */
    List<User> handle(GetAllUsersQuery query);
}

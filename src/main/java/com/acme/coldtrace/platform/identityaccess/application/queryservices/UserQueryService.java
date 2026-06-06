package com.acme.coldtrace.platform.identityaccess.application.queryservices;

import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetUsersByOrganizationIdQuery;
import com.acme.coldtrace.platform.shared.application.result.Result;

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
     * @return success with users, possibly empty, or failure when the organization does not exist
     * @see GetUsersByOrganizationIdQuery
     */
    Result<List<User>, UserQueryFailure> handle(GetUsersByOrganizationIdQuery query);
}

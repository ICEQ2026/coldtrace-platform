package com.acme.coldtrace.platform.identityaccess.application.internal.queryservices;

import com.acme.coldtrace.platform.identityaccess.application.queryservices.UserQueryService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetUsersByOrganizationIdQuery;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for user query operations.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserQueryServicesImpl implements UserQueryService {
    private final UserRepository userRepository;

    public UserQueryServicesImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves users from persistence by organization.
     *
     * @param query query object containing the organization identifier
     * @return users for the organization, possibly empty
     * @see GetUsersByOrganizationIdQuery
     */
    @Override
    public List<User> handle(GetUsersByOrganizationIdQuery query) {
        log.debug("Querying users by organizationId={}", query.organizationId());
        var users = userRepository.findAllByOrganizationId(query.organizationId());
        log.debug("Found {} users for organizationId={}", users.size(), query.organizationId());
        return users;
    }
}

package com.acme.coldtrace.platform.identityaccess.application.internal.queryservices;

import com.acme.coldtrace.platform.identityaccess.application.queryservices.UserQueryService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.queries.GetAllUsersQuery;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.UserRepository;
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
     * Retrieves all users from persistence.
     *
     * @param query query object representing the all-users request
     * @return list of users, possibly empty
     * @see GetAllUsersQuery
     */
    @Override
    public List<User> handle(GetAllUsersQuery query) {
        log.debug("Querying all users");
        var users = userRepository.findAll();
        log.debug("Found {} users", users.size());
        return users;
    }
}

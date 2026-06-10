package com.acme.coldtrace.platform.identityaccess.application.internal.commandservices;

import com.acme.coldtrace.platform.identityaccess.application.commandservices.UserCommandFailure;
import com.acme.coldtrace.platform.identityaccess.application.commandservices.UserCommandService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.AssignUserRoleCommand;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateUserCommand;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.RoleRepository;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for user command operations.
 * It enforces application-level creation rules such as unique email and valid
 * organization-role references before persisting the user aggregate.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Handles creation of a user aggregate.
     *
     * @param command command containing user data and references
     * @return success with the created user or failure with a command failure type
     * @throws DataIntegrityViolationException if an unexpected persistence constraint is violated
     * @see CreateUserCommand
     */
    @Override
    @Transactional
    public Result<User, UserCommandFailure> handle(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            log.warn("Duplicate user email detected: {}", command.email());
            return Result.failure(new UserCommandFailure.DuplicateEmail());
        }
        if (!organizationRepository.existsById(command.organizationId())) {
            log.warn("Organization not found for user creation: organizationId={}", command.organizationId());
            return Result.failure(new UserCommandFailure.OrganizationNotFound());
        }
        if (!roleRepository.existsById(command.roleId())) {
            log.warn("Role not found for user creation: roleId={}", command.roleId());
            return Result.failure(new UserCommandFailure.RoleNotFound());
        }

        try {
            var user = userRepository.save(new User(command));
            log.info("User created: id={}, email={}, organizationId={}, roleId={}",
                    user.getId(), user.getEmail(), user.getOrganizationId(), user.getRoleId());
            return Result.success(user);
        } catch (DataIntegrityViolationException exception) {
            if (userRepository.existsByEmail(command.email())) {
                log.warn("Duplicate user email detected by constraint: {}", command.email());
                return Result.failure(new UserCommandFailure.DuplicateEmail());
            }
            throw exception;
        }
    }

    /**
     * Handles the assignment of a role to an existing user.
     * <p>
     * The operation is intentionally scoped by organization. A user found in
     * another organization is treated as not found for this route, which keeps
     * the REST contract aligned with organization-owned user management.
     *
     * @param command command containing organization, user and target role identifiers
     * @return success with the updated user or failure with a command failure type
     * @see AssignUserRoleCommand
     */
    @Override
    @Transactional
    public Result<User, UserCommandFailure> handle(AssignUserRoleCommand command) {
        if (!organizationRepository.existsById(command.organizationId())) {
            log.warn("Organization not found for user role assignment: organizationId={}", command.organizationId());
            return Result.failure(new UserCommandFailure.OrganizationNotFound());
        }
        var user = userRepository.findByIdAndOrganizationId(command.userId(), command.organizationId());
        if (user.isEmpty()) {
            log.warn("User not found for role assignment: organizationId={}, userId={}",
                    command.organizationId(), command.userId());
            return Result.failure(new UserCommandFailure.UserNotFound());
        }
        if (!roleRepository.existsById(command.roleId())) {
            log.warn("Role not found for user role assignment: roleId={}", command.roleId());
            return Result.failure(new UserCommandFailure.RoleNotFound());
        }

        var userToUpdate = user.get();
        userToUpdate.assignRole(command);
        var updatedUser = userRepository.save(userToUpdate);
        log.info("User role assigned: userId={}, organizationId={}, roleId={}",
                updatedUser.getId(), updatedUser.getOrganizationId(), updatedUser.getRoleId());
        return Result.success(updatedUser);
    }
}

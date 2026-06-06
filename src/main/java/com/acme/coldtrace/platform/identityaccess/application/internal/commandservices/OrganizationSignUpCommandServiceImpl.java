package com.acme.coldtrace.platform.identityaccess.application.internal.commandservices;

import com.acme.coldtrace.platform.identityaccess.application.commandservices.OrganizationSignUpCommandFailure;
import com.acme.coldtrace.platform.identityaccess.application.commandservices.OrganizationSignUpCommandResult;
import com.acme.coldtrace.platform.identityaccess.application.commandservices.OrganizationSignUpCommandService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.User;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateOrganizationSignUpCommand;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.OrganizationRepository;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.RoleRepository;
import com.acme.coldtrace.platform.identityaccess.infrastructure.persistence.jpa.UserRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for organization sign-up.
 * It creates the organization and the first user in a single transaction.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class OrganizationSignUpCommandServiceImpl implements OrganizationSignUpCommandService {
    private static final String INITIAL_ROLE_NAME = "super-admin";

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public OrganizationSignUpCommandServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Handles organization sign-up by creating an organization and its first user.
     *
     * @param command command containing organization and first-user data
     * @return success with both created aggregates or failure with a sign-up command error
     * @see CreateOrganizationSignUpCommand
     */
    @Override
    @Transactional
    public Result<OrganizationSignUpCommandResult, OrganizationSignUpCommandFailure> handle(
            CreateOrganizationSignUpCommand command
    ) {
        if (organizationRepository.existsByContactEmailIgnoreCase(command.contactEmail())) {
            log.warn("Duplicate organization contact email detected during sign-up: {}", command.contactEmail());
            return Result.failure(new OrganizationSignUpCommandFailure.DuplicateOrganizationContactEmail());
        }
        if (command.taxId() != null && organizationRepository.existsByTaxIdIgnoreCase(command.taxId())) {
            log.warn("Duplicate organization tax id detected during sign-up: {}", command.taxId());
            return Result.failure(new OrganizationSignUpCommandFailure.DuplicateOrganizationTaxId());
        }
        if (userRepository.existsByEmailIgnoreCase(command.email())) {
            log.warn("Duplicate user email detected during sign-up: {}", command.email());
            return Result.failure(new OrganizationSignUpCommandFailure.DuplicateUserEmail());
        }

        var initialRole = roleRepository.findByName(INITIAL_ROLE_NAME);
        if (initialRole.isEmpty()) {
            log.error("Initial sign-up role not found: {}", INITIAL_ROLE_NAME);
            return Result.failure(new OrganizationSignUpCommandFailure.InitialRoleNotFound());
        }

        var organization = organizationRepository.save(new Organization(command.toCreateOrganizationCommand()));
        var user = userRepository.save(new User(command.toCreateUserCommand(organization.getId(), initialRole.get().getId())));

        log.info("Organization sign-up completed: organizationId={}, userId={}, role={}",
                organization.getId(), user.getId(), INITIAL_ROLE_NAME);
        return Result.success(new OrganizationSignUpCommandResult(organization, user));
    }
}

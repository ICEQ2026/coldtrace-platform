package com.acme.coldtrace.platform.identityaccess.application.internal.commandservices;

import com.acme.coldtrace.platform.identityaccess.application.commandservices.OrganizationCommandFailure;
import com.acme.coldtrace.platform.identityaccess.application.commandservices.OrganizationCommandService;
import com.acme.coldtrace.platform.identityaccess.domain.model.aggregates.Organization;
import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateOrganizationCommand;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementation for organization command operations.
 * It orchestrates organization aggregate creation and delegates persistence to
 * the organization repository.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class OrganizationCommandServiceImpl implements OrganizationCommandService {
    private final OrganizationRepository organizationRepository;

    public OrganizationCommandServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * Handles creation of an organization aggregate.
     *
     * @param command command containing organization data
     * @return success with created organization or failure with an organization command error
     * @throws IllegalArgumentException if command data is invalid
     * @see CreateOrganizationCommand
     */
    @Override
    @Transactional
    public Result<Organization, OrganizationCommandFailure> handle(CreateOrganizationCommand command) {
        if (organizationRepository.existsByContactEmail(command.contactEmail())) {
            log.warn("Duplicate organization contact email detected: {}", command.contactEmail());
            return Result.failure(new OrganizationCommandFailure.DuplicateContactEmail());
        }
        if (command.taxId() != null && organizationRepository.existsByTaxId(command.taxId())) {
            log.warn("Duplicate organization tax id detected: {}", command.taxId());
            return Result.failure(new OrganizationCommandFailure.DuplicateTaxId());
        }
        var organization = organizationRepository.save(new Organization(command));
        log.info("Organization created: id={}, contactEmail={}", organization.getId(), organization.getContactEmail());
        return Result.success(organization);
    }
}

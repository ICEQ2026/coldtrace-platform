package com.acme.coldtrace.platform.identityaccess.domain.model.aggregates;

import com.acme.coldtrace.platform.identityaccess.domain.model.commands.CreateOrganizationCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Organization aggregate root for the identity access context.
 * It represents the company account created during the organization sign-up flow.
 *
 * @since 1.0
 */
@Getter
@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false)
    private String commercialName;

    private String taxId;

    @Column(nullable = false)
    private String contactEmail;

    protected Organization() {
    }

    /**
     * Creates an organization from a create command.
     *
     * @param command command containing organization legal, commercial, tax, and contact data
     * @see CreateOrganizationCommand
     */
    public Organization(CreateOrganizationCommand command) {
        this.legalName = command.legalName();
        this.commercialName = command.commercialName();
        this.taxId = command.taxId();
        this.contactEmail = command.contactEmail();
    }
}

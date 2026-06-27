package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * JPA persistence entity for organization subscriptions.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "organization_subscriptions")
public class OrganizationSubscriptionPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, unique = true)
    private Long organizationId;

    @Column(nullable = false, length = 40)
    private String planCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BillingProvider provider;

    @Column
    private String providerCustomerId;

    @Column
    private String providerSubscriptionId;

    @Column
    private OffsetDateTime currentPeriodStart;

    @Column
    private OffsetDateTime currentPeriodEnd;

    @Column(nullable = false)
    private Boolean cancelAtPeriodEnd;

    @Column(length = 2000)
    private String metadata;
}

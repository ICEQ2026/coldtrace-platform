package com.acme.coldtrace.platform.billing.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingWebhookEventStatus;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * JPA persistence entity for handled billing provider webhook events.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(
        name = "billing_webhook_events",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_billing_webhook_events_provider_event",
                columnNames = {"provider", "event_id"}
        )
)
public class BillingWebhookEventPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BillingProvider provider;

    @Column(name = "event_id", nullable = false, length = 120)
    private String eventId;

    @Column(nullable = false, length = 120)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BillingWebhookEventStatus status;

    @Column
    private Long organizationId;

    @Column
    private String providerCustomerId;

    @Column
    private String providerSubscriptionId;

    @Column(nullable = false)
    private OffsetDateTime processedAt;

    @Column(length = 2000)
    private String metadata;
}

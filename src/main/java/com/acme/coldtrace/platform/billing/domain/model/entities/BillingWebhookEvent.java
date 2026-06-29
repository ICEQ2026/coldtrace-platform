package com.acme.coldtrace.platform.billing.domain.model.entities;

import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingWebhookEventStatus;
import lombok.Getter;

import java.time.OffsetDateTime;

/**
 * Stored provider webhook event used to keep Stripe processing idempotent.
 *
 * @since 1.0
 */
@Getter
public class BillingWebhookEvent {
    private final Long id;
    private final BillingProvider provider;
    private final String eventId;
    private final String eventType;
    private final BillingWebhookEventStatus status;
    private final Long organizationId;
    private final String providerCustomerId;
    private final String providerSubscriptionId;
    private final OffsetDateTime processedAt;
    private final String metadata;

    public BillingWebhookEvent(
            Long id,
            BillingProvider provider,
            String eventId,
            String eventType,
            BillingWebhookEventStatus status,
            Long organizationId,
            String providerCustomerId,
            String providerSubscriptionId,
            OffsetDateTime processedAt,
            String metadata
    ) {
        this.id = id;
        this.provider = provider == null ? BillingProvider.STRIPE : provider;
        this.eventId = requireText(eventId, "eventId");
        this.eventType = requireText(eventType, "eventType");
        this.status = status == null ? BillingWebhookEventStatus.PROCESSED : status;
        this.organizationId = organizationId;
        this.providerCustomerId = normalizeOptionalText(providerCustomerId);
        this.providerSubscriptionId = normalizeOptionalText(providerSubscriptionId);
        this.processedAt = processedAt == null ? OffsetDateTime.now() : processedAt;
        this.metadata = normalizeOptionalText(metadata);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("billing.webhook-event.error.%s.required".formatted(fieldName));
        }
        return value.trim();
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

package com.acme.coldtrace.platform.billing.infrastructure.stripe;

import com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook.BillingWebhookProviderEvent;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook.BillingWebhookProviderFailure;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook.BillingWebhookProviderService;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.acme.coldtrace.platform.billing.infrastructure.configuration.BillingStripeProperties;
import com.acme.coldtrace.platform.shared.application.result.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Stripe-backed webhook verifier and event normalizer.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class StripeBillingWebhookService implements BillingWebhookProviderService {
    private static final String PROVIDER = "STRIPE";
    private static final String EVENT_CHECKOUT_COMPLETED = "checkout.session.completed";
    private static final String EVENT_SUBSCRIPTION_UPDATED = "customer.subscription.updated";
    private static final String EVENT_SUBSCRIPTION_DELETED = "customer.subscription.deleted";
    private static final String EVENT_INVOICE_PAID = "invoice.paid";
    private static final String EVENT_INVOICE_PAYMENT_FAILED = "invoice.payment_failed";

    private final BillingStripeProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StripeBillingWebhookService(BillingStripeProperties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<BillingWebhookProviderEvent, BillingWebhookProviderFailure> parseSignedEvent(
            String payload,
            String signatureHeader
    ) {
        if (!properties.hasWebhookConfiguration()) {
            log.warn("Stripe webhook configuration is incomplete");
            return Result.failure(BillingWebhookProviderFailure.NOT_CONFIGURED);
        }
        if (signatureHeader == null || signatureHeader.isBlank()) {
            log.warn("Stripe webhook rejected because Stripe-Signature is missing");
            return Result.failure(BillingWebhookProviderFailure.MISSING_SIGNATURE);
        }

        try {
            var event = Webhook.constructEvent(payload, signatureHeader, properties.webhookSigningSecret());
            var root = objectMapper.readTree(payload);
            var object = root.path("data").path("object");
            return Result.success(toProviderEvent(
                    event.getId(),
                    event.getType(),
                    object
            ));
        } catch (SignatureVerificationException exception) {
            log.warn("Stripe webhook signature verification failed: {}", exception.getMessage());
            return Result.failure(BillingWebhookProviderFailure.INVALID_SIGNATURE);
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            log.warn("Stripe webhook payload parsing failed: {}", exception.getMessage());
            return Result.failure(BillingWebhookProviderFailure.INVALID_PAYLOAD);
        }
    }

    private BillingWebhookProviderEvent toProviderEvent(String eventId, String eventType, JsonNode object) {
        return switch (eventType) {
            case EVENT_CHECKOUT_COMPLETED -> checkoutCompleted(eventId, eventType, object);
            case EVENT_SUBSCRIPTION_UPDATED -> subscriptionUpdated(eventId, eventType, object);
            case EVENT_SUBSCRIPTION_DELETED -> subscriptionDeleted(eventId, eventType, object);
            case EVENT_INVOICE_PAID -> invoiceEvent(eventId, eventType, object, SubscriptionStatus.ACTIVE);
            case EVENT_INVOICE_PAYMENT_FAILED -> invoiceEvent(eventId, eventType, object, SubscriptionStatus.PAST_DUE);
            default -> unsupported(eventId, eventType, object);
        };
    }

    private BillingWebhookProviderEvent checkoutCompleted(String eventId, String eventType, JsonNode object) {
        return new BillingWebhookProviderEvent(
                PROVIDER,
                eventId,
                eventType,
                textAt(object, "id"),
                organizationIdFromCheckoutSession(object),
                textOrObjectIdAt(object, "customer"),
                textOrObjectIdAt(object, "subscription"),
                metadataText(object, "targetPlanCode"),
                null,
                SubscriptionStatus.ACTIVE,
                null,
                null,
                false,
                true
        );
    }

    private BillingWebhookProviderEvent subscriptionUpdated(String eventId, String eventType, JsonNode object) {
        return subscriptionEvent(eventId, eventType, object, statusFromStripeSubscription(object));
    }

    private BillingWebhookProviderEvent subscriptionDeleted(String eventId, String eventType, JsonNode object) {
        return subscriptionEvent(eventId, eventType, object, SubscriptionStatus.CANCELED);
    }

    private BillingWebhookProviderEvent subscriptionEvent(
            String eventId,
            String eventType,
            JsonNode object,
            SubscriptionStatus status
    ) {
        return new BillingWebhookProviderEvent(
                PROVIDER,
                eventId,
                eventType,
                textAt(object, "id"),
                metadataLong(object, "organizationId"),
                textOrObjectIdAt(object, "customer"),
                textAt(object, "id"),
                metadataText(object, "targetPlanCode"),
                subscriptionPriceId(object),
                status,
                epochSecondsAt(object, "current_period_start"),
                epochSecondsAt(object, "current_period_end"),
                booleanAt(object, "cancel_at_period_end"),
                true
        );
    }

    private BillingWebhookProviderEvent invoiceEvent(
            String eventId,
            String eventType,
            JsonNode object,
            SubscriptionStatus status
    ) {
        return new BillingWebhookProviderEvent(
                PROVIDER,
                eventId,
                eventType,
                textAt(object, "id"),
                null,
                textOrObjectIdAt(object, "customer"),
                invoiceSubscriptionId(object),
                null,
                invoicePriceId(object),
                status,
                epochSecondsAt(object, "period_start"),
                epochSecondsAt(object, "period_end"),
                false,
                true
        );
    }

    private BillingWebhookProviderEvent unsupported(String eventId, String eventType, JsonNode object) {
        return new BillingWebhookProviderEvent(
                PROVIDER,
                eventId,
                eventType,
                textAt(object, "id"),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false
        );
    }

    private Long organizationIdFromCheckoutSession(JsonNode object) {
        var metadataOrganizationId = metadataLong(object, "organizationId");
        if (metadataOrganizationId != null) {
            return metadataOrganizationId;
        }
        return longFromText(textAt(object, "client_reference_id"));
    }

    private SubscriptionStatus statusFromStripeSubscription(JsonNode object) {
        return switch (textAt(object, "status")) {
            case "active", "trialing" -> SubscriptionStatus.ACTIVE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            default -> SubscriptionStatus.PAST_DUE;
        };
    }

    private String invoiceSubscriptionId(JsonNode object) {
        var directSubscription = textOrObjectIdAt(object, "subscription");
        if (directSubscription != null) {
            return directSubscription;
        }
        return textAt(object.path("parent").path("subscription_details"), "subscription");
    }

    private String subscriptionPriceId(JsonNode object) {
        return textAt(object.path("items").path("data").path(0).path("price"), "id");
    }

    private String invoicePriceId(JsonNode object) {
        var legacyPriceId = textAt(object.path("lines").path("data").path(0).path("price"), "id");
        if (legacyPriceId != null) {
            return legacyPriceId;
        }
        return textAt(
                object.path("lines").path("data").path(0).path("pricing").path("price_details"),
                "price"
        );
    }

    private String metadataText(JsonNode object, String fieldName) {
        return textAt(object.path("metadata"), fieldName);
    }

    private Long metadataLong(JsonNode object, String fieldName) {
        return longFromText(metadataText(object, fieldName));
    }

    private OffsetDateTime epochSecondsAt(JsonNode object, String fieldName) {
        var value = object.path(fieldName);
        if (!value.canConvertToLong()) {
            return null;
        }
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(value.asLong()), ZoneOffset.UTC);
    }

    private Boolean booleanAt(JsonNode object, String fieldName) {
        var value = object.path(fieldName);
        if (!value.isBoolean()) {
            return false;
        }
        return value.asBoolean();
    }

    private String textOrObjectIdAt(JsonNode object, String fieldName) {
        var value = object.path(fieldName);
        if (value.isTextual()) {
            return normalize(value.asText());
        }
        if (value.isObject()) {
            return textAt(value, "id");
        }
        return null;
    }

    private String textAt(JsonNode object, String fieldName) {
        if (object == null || object.isMissingNode() || object.isNull()) {
            return null;
        }
        var value = object.path(fieldName);
        if (!value.isTextual()) {
            return null;
        }
        return normalize(value.asText());
    }

    private Long longFromText(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

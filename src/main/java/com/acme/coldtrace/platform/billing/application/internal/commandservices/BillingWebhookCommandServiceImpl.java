package com.acme.coldtrace.platform.billing.application.internal.commandservices;

import com.acme.coldtrace.platform.billing.application.commandservices.BillingWebhookCommandFailure;
import com.acme.coldtrace.platform.billing.application.commandservices.BillingWebhookCommandService;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook.BillingWebhookProviderEvent;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook.BillingWebhookProviderFailure;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.webhook.BillingWebhookProviderService;
import com.acme.coldtrace.platform.billing.application.model.BillingWebhookProcessingResult;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.domain.model.commands.ProcessStripeWebhookCommand;
import com.acme.coldtrace.platform.billing.domain.model.entities.BillingWebhookEvent;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingWebhookEventStatus;
import com.acme.coldtrace.platform.billing.domain.repositories.BillingWebhookEventRepository;
import com.acme.coldtrace.platform.billing.domain.repositories.OrganizationSubscriptionRepository;
import com.acme.coldtrace.platform.billing.domain.repositories.SubscriptionPlanRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Application service implementation for signed billing provider webhook processing.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class BillingWebhookCommandServiceImpl implements BillingWebhookCommandService {
    private static final String BASE_PLAN_CODE = "base";

    private final BillingWebhookProviderService billingWebhookProviderService;
    private final BillingWebhookEventRepository billingWebhookEventRepository;
    private final OrganizationSubscriptionRepository organizationSubscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public BillingWebhookCommandServiceImpl(
            BillingWebhookProviderService billingWebhookProviderService,
            BillingWebhookEventRepository billingWebhookEventRepository,
            OrganizationSubscriptionRepository organizationSubscriptionRepository,
            SubscriptionPlanRepository subscriptionPlanRepository
    ) {
        this.billingWebhookProviderService = billingWebhookProviderService;
        this.billingWebhookEventRepository = billingWebhookEventRepository;
        this.organizationSubscriptionRepository = organizationSubscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Result<BillingWebhookProcessingResult, BillingWebhookCommandFailure> handle(
            ProcessStripeWebhookCommand command
    ) {
        var providerResult = billingWebhookProviderService.parseSignedEvent(
                command.payload(),
                command.signatureHeader()
        );

        return providerResult.fold(
                this::processVerifiedEvent,
                failure -> Result.failure(toCommandFailure(failure))
        );
    }

    private Result<BillingWebhookProcessingResult, BillingWebhookCommandFailure> processVerifiedEvent(
            BillingWebhookProviderEvent event
    ) {
        var provider = BillingProvider.valueOf(event.provider());
        if (billingWebhookEventRepository.existsByProviderAndEventId(provider, event.eventId())) {
            log.info("Stripe webhook event already processed: eventId={}, eventType={}",
                    event.eventId(), event.eventType());
            return Result.success(new BillingWebhookProcessingResult(
                    event.provider(),
                    event.eventId(),
                    event.eventType(),
                    BillingWebhookEventStatus.PROCESSED.name(),
                    true,
                    event.organizationId(),
                    null,
                    null
            ));
        }

        if (!Boolean.TRUE.equals(event.supported())) {
            saveWebhookEvent(event, BillingWebhookEventStatus.IGNORED, null);
            log.debug("Stripe webhook event ignored: eventId={}, eventType={}", event.eventId(), event.eventType());
            return Result.success(new BillingWebhookProcessingResult(
                    event.provider(),
                    event.eventId(),
                    event.eventType(),
                    BillingWebhookEventStatus.IGNORED.name(),
                    false,
                    null,
                    null,
                    null
            ));
        }

        var subscription = findTargetSubscription(event);
        if (subscription.isEmpty()) {
            saveWebhookEvent(event, BillingWebhookEventStatus.IGNORED, "subscription-not-found");
            log.warn("Stripe webhook had no matching organization subscription: eventId={}, eventType={}",
                    event.eventId(), event.eventType());
            return Result.success(new BillingWebhookProcessingResult(
                    event.provider(),
                    event.eventId(),
                    event.eventType(),
                    BillingWebhookEventStatus.IGNORED.name(),
                    false,
                    event.organizationId(),
                    null,
                    null
            ));
        }

        var plan = resolvePlan(event, subscription.get());
        if (plan.isEmpty()) {
            saveWebhookEvent(event, BillingWebhookEventStatus.IGNORED, "plan-not-found");
            log.warn("Stripe webhook had no matching local plan: eventId={}, eventType={}, targetPlanCode={}, priceId={}",
                    event.eventId(), event.eventType(), event.targetPlanCode(), event.stripePriceId());
            return Result.success(new BillingWebhookProcessingResult(
                    event.provider(),
                    event.eventId(),
                    event.eventType(),
                    BillingWebhookEventStatus.IGNORED.name(),
                    false,
                    subscription.get().getOrganizationId(),
                    null,
                    null
            ));
        }

        var updatedSubscription = synchronizeSubscription(subscription.get(), plan.get(), event, provider);
        saveWebhookEvent(event, BillingWebhookEventStatus.PROCESSED, null);
        log.info("Stripe webhook synchronized subscription: organizationId={}, eventId={}, eventType={}, planCode={}, status={}",
                updatedSubscription.getOrganizationId(),
                event.eventId(),
                event.eventType(),
                updatedSubscription.getPlanCode(),
                updatedSubscription.getStatus());
        return Result.success(new BillingWebhookProcessingResult(
                event.provider(),
                event.eventId(),
                event.eventType(),
                BillingWebhookEventStatus.PROCESSED.name(),
                false,
                updatedSubscription.getOrganizationId(),
                updatedSubscription.getPlanCode(),
                updatedSubscription.getStatus().name()
        ));
    }

    private Optional<OrganizationSubscription> findTargetSubscription(BillingWebhookProviderEvent event) {
        if (event.providerSubscriptionId() != null) {
            var subscription = organizationSubscriptionRepository.findByProviderSubscriptionId(
                    event.providerSubscriptionId()
            );
            if (subscription.isPresent()) {
                return subscription;
            }
        }
        if (event.providerCustomerId() != null) {
            var subscription = organizationSubscriptionRepository.findByProviderCustomerId(event.providerCustomerId());
            if (subscription.isPresent()) {
                return subscription;
            }
        }
        if (event.organizationId() != null) {
            return organizationSubscriptionRepository.findByOrganizationId(event.organizationId());
        }
        return Optional.empty();
    }

    private Optional<SubscriptionPlan> resolvePlan(
            BillingWebhookProviderEvent event,
            OrganizationSubscription subscription
    ) {
        if (event.targetPlanCode() != null) {
            var plan = subscriptionPlanRepository.findByCode(event.targetPlanCode());
            if (plan.isPresent()) {
                return plan;
            }
        }
        if (event.stripePriceId() != null) {
            var plan = subscriptionPlanRepository.findByStripePriceId(event.stripePriceId());
            if (plan.isPresent()) {
                return plan;
            }
        }
        return subscriptionPlanRepository.findByCode(subscription.getPlanCode())
                .or(() -> subscriptionPlanRepository.findByCode(BASE_PLAN_CODE));
    }

    private OrganizationSubscription synchronizeSubscription(
            OrganizationSubscription subscription,
            SubscriptionPlan plan,
            BillingWebhookProviderEvent event,
            BillingProvider provider
    ) {
        subscription.synchronizeProviderState(
                plan.getCode(),
                event.subscriptionStatus(),
                provider,
                firstNonBlank(event.providerCustomerId(), subscription.getProviderCustomerId()),
                firstNonBlank(event.providerSubscriptionId(), subscription.getProviderSubscriptionId()),
                event.currentPeriodStart(),
                event.currentPeriodEnd(),
                event.cancelAtPeriodEnd(),
                metadataFor(event)
        );
        return organizationSubscriptionRepository.save(subscription);
    }

    private void saveWebhookEvent(
            BillingWebhookProviderEvent event,
            BillingWebhookEventStatus status,
            String reason
    ) {
        billingWebhookEventRepository.save(new BillingWebhookEvent(
                null,
                BillingProvider.valueOf(event.provider()),
                event.eventId(),
                event.eventType(),
                status,
                event.organizationId(),
                event.providerCustomerId(),
                event.providerSubscriptionId(),
                OffsetDateTime.now(),
                reason == null ? metadataFor(event) : "%s; reason=%s".formatted(metadataFor(event), reason)
        ));
    }

    private String metadataFor(BillingWebhookProviderEvent event) {
        return "objectId=%s; priceId=%s".formatted(
                safe(event.objectId()),
                safe(event.stripePriceId())
        );
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    private String safe(String value) {
        return value == null ? "none" : value;
    }

    private BillingWebhookCommandFailure toCommandFailure(BillingWebhookProviderFailure failure) {
        return switch (failure) {
            case NOT_CONFIGURED -> new BillingWebhookCommandFailure.ProviderNotConfigured();
            case MISSING_SIGNATURE -> new BillingWebhookCommandFailure.MissingSignature();
            case INVALID_SIGNATURE -> new BillingWebhookCommandFailure.InvalidSignature();
            case INVALID_PAYLOAD -> new BillingWebhookCommandFailure.InvalidPayload();
        };
    }
}

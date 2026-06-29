package com.acme.coldtrace.platform.billing.domain.model.aggregates;

import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Locale;

/**
 * Current billing subscription owned by one ColdTrace organization.
 *
 * @since 1.0
 */
@Getter
public class OrganizationSubscription extends AbstractDomainAggregateRoot<OrganizationSubscription> {
    private static final String BASE_PLAN_CODE = "base";

    private Long id;
    private Long organizationId;
    private String planCode;
    private SubscriptionStatus status;
    private BillingProvider provider;
    private String providerCustomerId;
    private String providerSubscriptionId;
    private OffsetDateTime currentPeriodStart;
    private OffsetDateTime currentPeriodEnd;
    private Boolean cancelAtPeriodEnd;
    private String metadata;

    protected OrganizationSubscription() {
    }

    public OrganizationSubscription(Long organizationId) {
        this(
                null,
                organizationId,
                BASE_PLAN_CODE,
                SubscriptionStatus.FREE,
                BillingProvider.NONE,
                null,
                null,
                null,
                null,
                false,
                null
        );
    }

    public OrganizationSubscription(
            Long id,
            Long organizationId,
            String planCode,
            SubscriptionStatus status,
            BillingProvider provider,
            String providerCustomerId,
            String providerSubscriptionId,
            OffsetDateTime currentPeriodStart,
            OffsetDateTime currentPeriodEnd,
            Boolean cancelAtPeriodEnd,
            String metadata
    ) {
        this.id = id;
        this.organizationId = requirePositive(organizationId, "organizationId");
        this.planCode = normalizePlanCode(planCode);
        this.status = status == null ? SubscriptionStatus.FREE : status;
        this.provider = provider == null ? BillingProvider.NONE : provider;
        this.providerCustomerId = normalizeOptionalText(providerCustomerId);
        this.providerSubscriptionId = normalizeOptionalText(providerSubscriptionId);
        this.currentPeriodStart = currentPeriodStart;
        this.currentPeriodEnd = currentPeriodEnd;
        this.cancelAtPeriodEnd = cancelAtPeriodEnd != null && cancelAtPeriodEnd;
        this.metadata = normalizeOptionalText(metadata);
    }

    /**
     * @return true when plan limits and feature flags should be exposed as enabled
     */
    public boolean allowsPlanEntitlements() {
        return status.allowsPlanEntitlements();
    }

    /**
     * Updates this subscription from a verified external provider event.
     *
     * @param planCode subscribed plan code
     * @param status current provider subscription status
     * @param provider billing provider
     * @param providerCustomerId provider customer identifier
     * @param providerSubscriptionId provider subscription identifier
     * @param currentPeriodStart current billing period start
     * @param currentPeriodEnd current billing period end
     * @param cancelAtPeriodEnd whether provider cancellation is scheduled
     * @param metadata non-sensitive synchronization metadata
     */
    public void synchronizeProviderState(
            String planCode,
            SubscriptionStatus status,
            BillingProvider provider,
            String providerCustomerId,
            String providerSubscriptionId,
            OffsetDateTime currentPeriodStart,
            OffsetDateTime currentPeriodEnd,
            Boolean cancelAtPeriodEnd,
            String metadata
    ) {
        this.planCode = normalizePlanCode(planCode);
        this.status = status == null ? SubscriptionStatus.FREE : status;
        this.provider = provider == null ? BillingProvider.NONE : provider;
        this.providerCustomerId = normalizeOptionalText(providerCustomerId);
        this.providerSubscriptionId = normalizeOptionalText(providerSubscriptionId);
        this.currentPeriodStart = currentPeriodStart;
        this.currentPeriodEnd = currentPeriodEnd;
        this.cancelAtPeriodEnd = cancelAtPeriodEnd != null && cancelAtPeriodEnd;
        this.metadata = normalizeOptionalText(metadata);
    }

    private static Long requirePositive(Long value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(
                    "billing.organization-subscription.error.%s.invalid".formatted(fieldName)
            );
        }
        return value;
    }

    private static String normalizePlanCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("billing.organization-subscription.error.planCode.required");
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

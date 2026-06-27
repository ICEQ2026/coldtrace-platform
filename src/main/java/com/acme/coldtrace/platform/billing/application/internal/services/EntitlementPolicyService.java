package com.acme.coldtrace.platform.billing.application.internal.services;

import com.acme.coldtrace.platform.billing.application.model.OrganizationEntitlement;
import com.acme.coldtrace.platform.billing.application.model.OrganizationSubscriptionUsage;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Computes effective entitlements from subscription status, plan limits and usage.
 *
 * @since 1.0
 */
@Service
public class EntitlementPolicyService {
    private static final String CATEGORY_LIMIT = "LIMIT";
    private static final String CATEGORY_FEATURE = "FEATURE";

    /**
     * Computes entitlements for one organization subscription.
     *
     * @param subscription current organization subscription
     * @param plan subscribed plan
     * @param usage current organization usage counters
     * @return computed entitlements
     */
    public List<OrganizationEntitlement> compute(
            OrganizationSubscription subscription,
            SubscriptionPlan plan,
            OrganizationSubscriptionUsage usage
    ) {
        return List.of(
                limit("locations", plan.getDisplayName(), subscription, plan.getUsageLimits().maxLocations(),
                        usage.locations()),
                limit("assets", plan.getDisplayName(), subscription, plan.getUsageLimits().maxAssets(),
                        usage.assets()),
                limit("iot-devices", plan.getDisplayName(), subscription, plan.getUsageLimits().maxIotDevices(),
                        usage.iotDevices()),
                limit("users", plan.getDisplayName(), subscription, plan.getUsageLimits().maxUsers(),
                        usage.users()),
                limit("report-history", plan.getDisplayName(), subscription,
                        plan.getUsageLimits().historyRetentionDays(), null),
                feature("exports", plan.getDisplayName(), subscription, plan.getFeatureFlags().allowsExports()),
                feature("maintenance", plan.getDisplayName(), subscription,
                        plan.getFeatureFlags().allowsMaintenance()),
                feature("ai-guidance", plan.getDisplayName(), subscription,
                        plan.getFeatureFlags().allowsAiGuidance()),
                feature("ai-report-summary", plan.getDisplayName(), subscription,
                        plan.getFeatureFlags().allowsAiReportSummary())
        );
    }

    private OrganizationEntitlement limit(
            String key,
            String planName,
            OrganizationSubscription subscription,
            Integer limit,
            Integer used
    ) {
        var statusAllows = subscription.allowsPlanEntitlements();
        var remaining = remaining(limit, used);
        var limitAvailable = limit == null || used == null || used < limit;
        var enabled = statusAllows && limitAvailable;
        return new OrganizationEntitlement(
                key,
                CATEGORY_LIMIT,
                enabled,
                limit,
                used,
                remaining,
                lockedReason(enabled, statusAllows, limitAvailable, planName, subscription)
        );
    }

    private OrganizationEntitlement feature(
            String key,
            String planName,
            OrganizationSubscription subscription,
            boolean included
    ) {
        var statusAllows = subscription.allowsPlanEntitlements();
        var enabled = statusAllows && included;
        return new OrganizationEntitlement(
                key,
                CATEGORY_FEATURE,
                enabled,
                null,
                null,
                null,
                lockedReason(enabled, statusAllows, included, planName, subscription)
        );
    }

    private Integer remaining(Integer limit, Integer used) {
        if (limit == null || used == null) {
            return null;
        }
        return Math.max(limit - used, 0);
    }

    private String lockedReason(
            Boolean enabled,
            boolean statusAllows,
            boolean planAllows,
            String planName,
            OrganizationSubscription subscription
    ) {
        if (Boolean.TRUE.equals(enabled)) {
            return null;
        }
        if (!statusAllows) {
            return "Subscription status %s does not unlock plan entitlements".formatted(subscription.getStatus());
        }
        if (!planAllows) {
            return "Current %s plan does not include available capacity for this entitlement".formatted(planName);
        }
        return "Entitlement is unavailable for the current subscription";
    }
}

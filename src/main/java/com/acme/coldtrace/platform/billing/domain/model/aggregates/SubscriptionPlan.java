package com.acme.coldtrace.platform.billing.domain.model.aggregates;

import com.acme.coldtrace.platform.billing.domain.model.valueobjects.PlanFeatureFlags;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.PlanUsageLimits;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Subscription plan aggregate for the billing bounded context.
 *
 * <p>The aggregate stores the public plan catalog used by the landing page,
 * frontend billing UI, and future backend entitlement checks.</p>
 *
 * @since 1.0
 */
@Getter
public class SubscriptionPlan extends AbstractDomainAggregateRoot<SubscriptionPlan> {
    private Long id;
    private String code;
    private String displayName;
    private String description;
    private String currency;
    private Integer monthlyPriceCents;
    private String stripePriceId;
    private Boolean recommended;
    private String recommendedLabel;
    private Boolean active;
    private PlanUsageLimits usageLimits;
    private PlanFeatureFlags featureFlags;
    private List<String> includedFeatures = new ArrayList<>();

    protected SubscriptionPlan() {
    }

    public SubscriptionPlan(
            String code,
            String displayName,
            String description,
            String currency,
            Integer monthlyPriceCents,
            String stripePriceId,
            Boolean recommended,
            String recommendedLabel,
            Boolean active,
            PlanUsageLimits usageLimits,
            PlanFeatureFlags featureFlags,
            List<String> includedFeatures
    ) {
        this(null, code, displayName, description, currency, monthlyPriceCents, stripePriceId, recommended,
                recommendedLabel, active, usageLimits, featureFlags, includedFeatures);
    }

    public SubscriptionPlan(
            Long id,
            String code,
            String displayName,
            String description,
            String currency,
            Integer monthlyPriceCents,
            String stripePriceId,
            Boolean recommended,
            String recommendedLabel,
            Boolean active,
            PlanUsageLimits usageLimits,
            PlanFeatureFlags featureFlags,
            List<String> includedFeatures
    ) {
        this.id = id;
        this.code = normalizeCode(code);
        this.displayName = requireText(displayName, "displayName");
        this.description = requireText(description, "description");
        this.currency = requireText(currency, "currency").toUpperCase(Locale.ROOT);
        this.monthlyPriceCents = requireNonNegative(monthlyPriceCents, "monthlyPriceCents");
        this.stripePriceId = normalizeOptionalText(stripePriceId);
        this.recommended = recommended != null && recommended;
        this.recommendedLabel = normalizeOptionalText(recommendedLabel);
        this.active = active != null && active;
        this.usageLimits = requireNonNull(usageLimits, "usageLimits");
        this.featureFlags = requireNonNull(featureFlags, "featureFlags");
        this.includedFeatures = normalizeFeatures(includedFeatures);
    }

    private static String normalizeCode(String value) {
        return requireText(value, "code").toLowerCase(Locale.ROOT);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("billing.subscription-plan.error.%s.required".formatted(fieldName));
        }
        return value.trim();
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static Integer requireNonNegative(Integer value, String fieldName) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("billing.subscription-plan.error.%s.invalid".formatted(fieldName));
        }
        return value;
    }

    private static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("billing.subscription-plan.error.%s.required".formatted(fieldName));
        }
        return value;
    }

    private static List<String> normalizeFeatures(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }
}

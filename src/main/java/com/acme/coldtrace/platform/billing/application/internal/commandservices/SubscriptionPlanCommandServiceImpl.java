package com.acme.coldtrace.platform.billing.application.internal.commandservices;

import com.acme.coldtrace.platform.billing.application.commandservices.SubscriptionPlanCommandService;
import com.acme.coldtrace.platform.billing.domain.model.aggregates.SubscriptionPlan;
import com.acme.coldtrace.platform.billing.domain.model.commands.SeedSubscriptionPlansCommand;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.PlanFeatureFlags;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.PlanUsageLimits;
import com.acme.coldtrace.platform.billing.domain.repositories.SubscriptionPlanRepository;
import com.acme.coldtrace.platform.billing.infrastructure.configuration.BillingPlanCatalogProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for subscription plan commands.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class SubscriptionPlanCommandServiceImpl implements SubscriptionPlanCommandService {
    private static final String CURRENCY_PEN = "PEN";

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final BillingPlanCatalogProperties planCatalogProperties;

    public SubscriptionPlanCommandServiceImpl(
            SubscriptionPlanRepository subscriptionPlanRepository,
            BillingPlanCatalogProperties planCatalogProperties
    ) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.planCatalogProperties = planCatalogProperties;
    }

    /**
     * Seeds Base, Operations, and Compliance AI plans.
     *
     * @param command seed command
     */
    @Override
    @Transactional
    public void handle(SeedSubscriptionPlansCommand command) {
        defaultPlans().forEach(this::seedPlan);
    }

    private void seedPlan(SubscriptionPlanSeed seed) {
        var existingPlan = subscriptionPlanRepository.findByCode(seed.code());
        var plan = seed.toDomain(existingPlan.map(SubscriptionPlan::getId).orElse(null));
        subscriptionPlanRepository.save(plan);
        log.debug("Seeded subscription plan {}", seed.code());
    }

    private List<SubscriptionPlanSeed> defaultPlans() {
        return List.of(
                new SubscriptionPlanSeed(
                        "base",
                        "Base",
                        "For small teams validating cold-chain monitoring.",
                        0,
                        null,
                        false,
                        null,
                        true,
                        new PlanUsageLimits(1, 2, 3, 3, 7),
                        new PlanFeatureFlags(false, false, false, false),
                        List.of("Basic monitoring", "In-app alerts", "Incident list", "Basic daily log")
                ),
                new SubscriptionPlanSeed(
                        "operations",
                        "Operations",
                        "For SMEs with recurring cold-chain monitoring and operational reporting.",
                        14900,
                        planCatalogProperties.operationsStripePriceId(),
                        true,
                        "Recommended",
                        true,
                        new PlanUsageLimits(3, 20, 50, 10, 365),
                        new PlanFeatureFlags(true, true, false, false),
                        List.of("Email alerts", "Operational reports", "Maintenance scheduling",
                                "CSV exports", "Full incident lifecycle")
                ),
                new SubscriptionPlanSeed(
                        "compliance-ai",
                        "Compliance AI",
                        "For multi-site quality teams that need compliance evidence and AI guidance.",
                        39900,
                        planCatalogProperties.complianceAiStripePriceId(),
                        false,
                        null,
                        true,
                        new PlanUsageLimits(10, 100, 250, 30, 730),
                        new PlanFeatureFlags(true, true, true, true),
                        List.of("Advanced compliance reports", "AI incident guidance", "AI report summaries",
                                "Priority support", "Expanded exports")
                )
        );
    }

    private record SubscriptionPlanSeed(
            String code,
            String displayName,
            String description,
            Integer monthlyPriceCents,
            String stripePriceId,
            Boolean recommended,
            String recommendedLabel,
            Boolean active,
            PlanUsageLimits usageLimits,
            PlanFeatureFlags featureFlags,
            List<String> includedFeatures
    ) {
        private SubscriptionPlan toDomain(Long id) {
            return new SubscriptionPlan(
                    id,
                    code,
                    displayName,
                    description,
                    CURRENCY_PEN,
                    monthlyPriceCents,
                    stripePriceId,
                    recommended,
                    recommendedLabel,
                    active,
                    usageLimits,
                    featureFlags,
                    includedFeatures
            );
        }
    }
}

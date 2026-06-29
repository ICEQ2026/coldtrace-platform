package com.acme.coldtrace.platform.billing.application.internal.eventhandlers;

import com.acme.coldtrace.platform.billing.application.commandservices.OrganizationSubscriptionCommandService;
import com.acme.coldtrace.platform.billing.application.commandservices.SubscriptionPlanCommandService;
import com.acme.coldtrace.platform.billing.domain.model.commands.SeedBaseOrganizationSubscriptionsCommand;
import com.acme.coldtrace.platform.billing.domain.model.commands.SeedSubscriptionPlansCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * Application lifecycle handler that ensures billing plans are seeded.
 *
 * @since 1.0
 */
@Service
@Slf4j
public class BillingApplicationReadyEventHandler {
    private final OrganizationSubscriptionCommandService organizationSubscriptionCommandService;
    private final SubscriptionPlanCommandService subscriptionPlanCommandService;

    public BillingApplicationReadyEventHandler(
            OrganizationSubscriptionCommandService organizationSubscriptionCommandService,
            SubscriptionPlanCommandService subscriptionPlanCommandService
    ) {
        this.organizationSubscriptionCommandService = organizationSubscriptionCommandService;
        this.subscriptionPlanCommandService = subscriptionPlanCommandService;
    }

    /**
     * Handles the Spring application-ready event and triggers plan seeding.
     *
     * @param event Spring Boot readiness event
     */
    @EventListener
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        log.info("Starting to verify if subscription plan seeding is needed for {} at {}",
                applicationName, currentTimestamp());
        subscriptionPlanCommandService.handle(new SeedSubscriptionPlansCommand());
        organizationSubscriptionCommandService.handle(new SeedBaseOrganizationSubscriptionsCommand());
        log.info("Subscription plan seeding verification finished for {} at {}",
                applicationName, currentTimestamp());
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}

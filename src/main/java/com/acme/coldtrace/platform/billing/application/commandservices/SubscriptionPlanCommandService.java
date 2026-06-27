package com.acme.coldtrace.platform.billing.application.commandservices;

import com.acme.coldtrace.platform.billing.domain.model.commands.SeedSubscriptionPlansCommand;

/**
 * Application service contract for subscription plan commands.
 *
 * @since 1.0
 */
public interface SubscriptionPlanCommandService {
    /**
     * Ensures the default plan catalog exists and is up to date.
     *
     * @param command seed command
     */
    void handle(SeedSubscriptionPlansCommand command);
}

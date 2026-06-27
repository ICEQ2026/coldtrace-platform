package com.acme.coldtrace.platform.billing.application.commandservices;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;
import com.acme.coldtrace.platform.billing.domain.model.commands.InitializeBaseOrganizationSubscriptionCommand;
import com.acme.coldtrace.platform.billing.domain.model.commands.SeedBaseOrganizationSubscriptionsCommand;

/**
 * Application service contract for organization subscription commands.
 *
 * @since 1.0
 */
public interface OrganizationSubscriptionCommandService {
    /**
     * Ensures one organization has a Base subscription.
     *
     * @param command command containing the organization identifier
     * @return existing or newly created organization subscription
     */
    OrganizationSubscription handle(InitializeBaseOrganizationSubscriptionCommand command);

    /**
     * Ensures all existing organizations have a Base subscription when missing.
     *
     * @param command seed command
     */
    void handle(SeedBaseOrganizationSubscriptionsCommand command);
}

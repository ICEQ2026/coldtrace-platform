package com.acme.coldtrace.platform.billing.application.commandservices;

import com.acme.coldtrace.platform.billing.application.model.BillingPortalSession;
import com.acme.coldtrace.platform.billing.domain.model.commands.CreateBillingPortalSessionCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for customer portal session commands.
 *
 * @since 1.0
 */
public interface BillingPortalSessionCommandService {
    /**
     * Handles customer portal session creation.
     *
     * @param command command with organization scope
     * @return created portal session or controlled failure
     */
    Result<BillingPortalSession, BillingPortalSessionCommandFailure> handle(
            CreateBillingPortalSessionCommand command
    );
}

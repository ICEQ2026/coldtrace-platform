package com.acme.coldtrace.platform.billing.application.commandservices;

import com.acme.coldtrace.platform.billing.application.model.BillingCheckoutSession;
import com.acme.coldtrace.platform.billing.domain.model.commands.CreateBillingCheckoutSessionCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for billing checkout sessions.
 *
 * @since 1.0
 */
public interface BillingCheckoutSessionCommandService {
    /**
     * Creates a provider-hosted checkout session for a paid plan.
     *
     * @param command checkout session creation command
     * @return checkout session or controlled failure
     */
    Result<BillingCheckoutSession, BillingCheckoutSessionCommandFailure> handle(
            CreateBillingCheckoutSessionCommand command
    );
}

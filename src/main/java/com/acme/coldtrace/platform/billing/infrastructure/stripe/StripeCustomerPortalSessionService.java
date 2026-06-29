package com.acme.coldtrace.platform.billing.infrastructure.stripe;

import com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal.PortalSessionProviderFailure;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal.PortalSessionProviderRequest;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal.PortalSessionProviderResult;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.portal.PortalSessionProviderService;
import com.acme.coldtrace.platform.billing.infrastructure.configuration.BillingStripeProperties;
import com.acme.coldtrace.platform.shared.application.result.Result;
import com.stripe.exception.StripeException;
import com.stripe.model.billingportal.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.billingportal.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Stripe-backed customer portal session provider adapter.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class StripeCustomerPortalSessionService implements PortalSessionProviderService {
    private static final String PROVIDER = "STRIPE";

    private final BillingStripeProperties properties;

    public StripeCustomerPortalSessionService(BillingStripeProperties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<PortalSessionProviderResult, PortalSessionProviderFailure> createCustomerPortalSession(
            PortalSessionProviderRequest request
    ) {
        if (!properties.hasCustomerPortalConfiguration()) {
            log.warn("Stripe customer portal configuration is incomplete");
            return Result.failure(PortalSessionProviderFailure.NOT_CONFIGURED);
        }

        try {
            var session = Session.create(toStripeSessionCreateParams(request), requestOptions());
            if (session.getUrl() == null || session.getUrl().isBlank()) {
                log.warn("Stripe customer portal session did not include a redirect URL: sessionId={}",
                        session.getId());
                return Result.failure(PortalSessionProviderFailure.UNAVAILABLE);
            }
            return Result.success(new PortalSessionProviderResult(PROVIDER, session.getId(), session.getUrl()));
        } catch (StripeException exception) {
            log.warn("Stripe customer portal session creation failed: organizationId={}, status={}, code={}",
                    request.organizationId(), exception.getStatusCode(), exception.getCode());
            return Result.failure(PortalSessionProviderFailure.UNAVAILABLE);
        }
    }

    private SessionCreateParams toStripeSessionCreateParams(PortalSessionProviderRequest request) {
        return SessionCreateParams.builder()
                .setCustomer(request.providerCustomerId())
                .setReturnUrl(properties.customerPortalReturnUrl())
                .build();
    }

    private RequestOptions requestOptions() {
        return RequestOptions.builder()
                .setApiKey(properties.secretKey())
                .build();
    }
}

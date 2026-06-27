package com.acme.coldtrace.platform.billing.infrastructure.stripe;

import com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout.CheckoutSessionProviderFailure;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout.CheckoutSessionProviderRequest;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout.CheckoutSessionProviderResult;
import com.acme.coldtrace.platform.billing.application.internal.outboundservices.checkout.CheckoutSessionProviderService;
import com.acme.coldtrace.platform.billing.infrastructure.configuration.BillingStripeProperties;
import com.acme.coldtrace.platform.shared.application.result.Result;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Stripe-backed checkout session provider adapter.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class StripeCheckoutSessionService implements CheckoutSessionProviderService {
    private static final String PROVIDER = "STRIPE";
    private static final String METADATA_ORGANIZATION_ID = "organizationId";
    private static final String METADATA_TARGET_PLAN_CODE = "targetPlanCode";

    private final BillingStripeProperties properties;

    public StripeCheckoutSessionService(BillingStripeProperties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result<CheckoutSessionProviderResult, CheckoutSessionProviderFailure> createSubscriptionCheckoutSession(
            CheckoutSessionProviderRequest request
    ) {
        if (!properties.hasCheckoutConfiguration()) {
            log.warn("Stripe checkout configuration is incomplete");
            return Result.failure(CheckoutSessionProviderFailure.NOT_CONFIGURED);
        }

        try {
            var session = Session.create(toStripeSessionCreateParams(request), requestOptions());
            if (session.getUrl() == null || session.getUrl().isBlank()) {
                log.warn("Stripe checkout session did not include a redirect URL: sessionId={}", session.getId());
                return Result.failure(CheckoutSessionProviderFailure.UNAVAILABLE);
            }
            return Result.success(new CheckoutSessionProviderResult(PROVIDER, session.getId(), session.getUrl()));
        } catch (StripeException exception) {
            log.warn("Stripe checkout session creation failed: organizationId={}, status={}, code={}",
                    request.organizationId(), exception.getStatusCode(), exception.getCode());
            return Result.failure(CheckoutSessionProviderFailure.UNAVAILABLE);
        }
    }

    private SessionCreateParams toStripeSessionCreateParams(CheckoutSessionProviderRequest request) {
        var metadataOrganizationId = request.organizationId().toString();
        var builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(properties.checkoutSuccessUrl())
                .setCancelUrl(properties.checkoutCancelUrl())
                .setClientReferenceId(metadataOrganizationId)
                .putMetadata(METADATA_ORGANIZATION_ID, metadataOrganizationId)
                .putMetadata(METADATA_TARGET_PLAN_CODE, request.targetPlanCode())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(request.stripePriceId())
                        .setQuantity(1L)
                        .build())
                .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                        .putMetadata(METADATA_ORGANIZATION_ID, metadataOrganizationId)
                        .putMetadata(METADATA_TARGET_PLAN_CODE, request.targetPlanCode())
                        .build());

        if (request.providerCustomerId() != null) {
            builder.setCustomer(request.providerCustomerId());
        }

        return builder.build();
    }

    private RequestOptions requestOptions() {
        return RequestOptions.builder()
                .setApiKey(properties.secretKey())
                .build();
    }
}

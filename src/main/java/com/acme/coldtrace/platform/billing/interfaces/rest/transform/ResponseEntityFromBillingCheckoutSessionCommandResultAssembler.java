package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.application.commandservices.BillingCheckoutSessionCommandFailure;
import com.acme.coldtrace.platform.billing.application.model.BillingCheckoutSession;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Interface layer translator for billing checkout session command results.
 *
 * @since 1.0
 */
public final class ResponseEntityFromBillingCheckoutSessionCommandResultAssembler {
    private ResponseEntityFromBillingCheckoutSessionCommandResultAssembler() {
    }

    /**
     * Converts a checkout session command result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or controlled error response
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<BillingCheckoutSession, BillingCheckoutSessionCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                session -> ResponseEntity.ok(BillingCheckoutSessionResourceFromResultAssembler.toResourceFromResult(
                        session
                )),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            BillingCheckoutSessionCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                messageSource.getMessage(
                        failure.messageKey(),
                        failure.args(),
                        failure.messageKey(),
                        LocaleContextHolder.getLocale()
                )
        ));
    }

    private static HttpStatus statusFromFailure(BillingCheckoutSessionCommandFailure failure) {
        if (failure instanceof BillingCheckoutSessionCommandFailure.OrganizationNotFound ||
                failure instanceof BillingCheckoutSessionCommandFailure.OrganizationSubscriptionNotFound ||
                failure instanceof BillingCheckoutSessionCommandFailure.TargetPlanNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        if (failure instanceof BillingCheckoutSessionCommandFailure.FreePlanCheckoutNotAllowed ||
                failure instanceof BillingCheckoutSessionCommandFailure.PlanProviderPriceNotConfigured) {
            return HttpStatus.CONFLICT;
        }
        if (failure instanceof BillingCheckoutSessionCommandFailure.ProviderNotConfigured) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        return HttpStatus.BAD_GATEWAY;
    }
}

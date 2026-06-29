package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.application.commandservices.BillingPortalSessionCommandFailure;
import com.acme.coldtrace.platform.billing.application.model.BillingPortalSession;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Interface layer translator for customer portal session command results.
 *
 * @since 1.0
 */
public final class ResponseEntityFromBillingPortalSessionCommandResultAssembler {
    private ResponseEntityFromBillingPortalSessionCommandResultAssembler() {
    }

    /**
     * Converts a portal session command result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or controlled error response
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<BillingPortalSession, BillingPortalSessionCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                session -> ResponseEntity.ok(BillingPortalSessionResourceFromResultAssembler.toResourceFromResult(
                        session
                )),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            BillingPortalSessionCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                messageSource.getMessage(
                        failure.messageKey(),
                        null,
                        failure.messageKey(),
                        LocaleContextHolder.getLocale()
                )
        ));
    }

    private static HttpStatus statusFromFailure(BillingPortalSessionCommandFailure failure) {
        if (failure instanceof BillingPortalSessionCommandFailure.OrganizationNotFound ||
                failure instanceof BillingPortalSessionCommandFailure.OrganizationSubscriptionNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        if (failure instanceof BillingPortalSessionCommandFailure.ProviderCustomerNotFound) {
            return HttpStatus.CONFLICT;
        }
        if (failure instanceof BillingPortalSessionCommandFailure.ProviderNotConfigured) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        return HttpStatus.BAD_GATEWAY;
    }
}

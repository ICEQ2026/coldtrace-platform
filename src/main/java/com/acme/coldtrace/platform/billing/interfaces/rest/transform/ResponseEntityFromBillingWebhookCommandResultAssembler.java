package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.application.commandservices.BillingWebhookCommandFailure;
import com.acme.coldtrace.platform.billing.application.model.BillingWebhookProcessingResult;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Interface layer translator for billing webhook command results.
 *
 * @since 1.0
 */
public final class ResponseEntityFromBillingWebhookCommandResultAssembler {
    private ResponseEntityFromBillingWebhookCommandResultAssembler() {
    }

    /**
     * Converts a webhook processing result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or controlled error response
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<BillingWebhookProcessingResult, BillingWebhookCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                processingResult -> ResponseEntity.ok(
                        BillingWebhookProcessingResourceFromResultAssembler.toResourceFromResult(processingResult)
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            BillingWebhookCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                messageSource.getMessage(
                        failure.messageKey(),
                        new Object[0],
                        failure.messageKey(),
                        LocaleContextHolder.getLocale()
                )
        ));
    }

    private static HttpStatus statusFromFailure(BillingWebhookCommandFailure failure) {
        if (failure instanceof BillingWebhookCommandFailure.ProviderNotConfigured) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        if (failure instanceof BillingWebhookCommandFailure.ProcessingFailed) {
            return HttpStatus.BAD_GATEWAY;
        }
        return HttpStatus.BAD_REQUEST;
    }
}

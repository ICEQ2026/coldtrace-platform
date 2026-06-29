package com.acme.coldtrace.platform.billing.interfaces.rest.transform;

import com.acme.coldtrace.platform.billing.application.model.OrganizationSubscriptionDetails;
import com.acme.coldtrace.platform.billing.application.queryservices.OrganizationSubscriptionQueryFailure;
import com.acme.coldtrace.platform.billing.interfaces.rest.resources.OrganizationSubscriptionResource;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Interface layer translator converting organization subscription query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromOrganizationSubscriptionQueryResultAssembler {
    /**
     * Converts a subscription query result into an HTTP response.
     *
     * @param result query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<OrganizationSubscriptionDetails, OrganizationSubscriptionQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                details -> ResponseEntity.ok(
                        OrganizationSubscriptionResourceFromResultAssembler.toResourceFromResult(details)
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            OrganizationSubscriptionQueryFailure failure,
            MessageSource messageSource
    ) {
        var status = HttpStatus.NOT_FOUND;
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
}

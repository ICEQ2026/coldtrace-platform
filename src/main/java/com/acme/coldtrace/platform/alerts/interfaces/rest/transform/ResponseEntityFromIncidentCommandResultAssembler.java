package com.acme.coldtrace.platform.alerts.interfaces.rest.transform;

import com.acme.coldtrace.platform.alerts.application.commandservices.IncidentCommandFailure;
import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Interface layer translator converting incident command results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromIncidentCommandResultAssembler {
    /**
     * Converts an incident creation result into an HTTP response.
     *
     * @param result incident command result
     * @param messageSource message source for localized failure details
     * @return 201 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromCreateResult(
            Result<Incident, IncidentCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                incident -> new ResponseEntity<>(
                        IncidentResourceFromEntityAssembler.toResourceFromEntity(incident),
                        CREATED
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts an incident state-change result into an HTTP response.
     *
     * @param result incident command result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromLifecycleResult(
            Result<Incident, IncidentCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                incident -> ResponseEntity.ok(IncidentResourceFromEntityAssembler.toResourceFromEntity(incident)),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            IncidentCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                localizeMessage(messageSource, failure.messageKey(), failure.args())
        ));
    }

    private static HttpStatus statusFromFailure(IncidentCommandFailure failure) {
        if (failure instanceof IncidentCommandFailure.OrganizationNotFound ||
                failure instanceof IncidentCommandFailure.IncidentNotFound ||
                failure instanceof IncidentCommandFailure.AssetNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        if (failure instanceof IncidentCommandFailure.AlreadyAcknowledged ||
                failure instanceof IncidentCommandFailure.AlreadyEscalated ||
                failure instanceof IncidentCommandFailure.AlreadyResolved ||
                failure instanceof IncidentCommandFailure.InvalidLifecycleTransition) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private static String localizeMessage(MessageSource messageSource, String messageKey, Object[] args) {
        return messageSource.getMessage(
                messageKey,
                args,
                messageKey,
                LocaleContextHolder.getLocale()
        );
    }
}

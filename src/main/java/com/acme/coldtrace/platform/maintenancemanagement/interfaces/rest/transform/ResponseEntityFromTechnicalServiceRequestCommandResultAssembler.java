package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.application.commandservices.TechnicalServiceRequestCommandFailure;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Assembler that converts technical service request command results into HTTP responses.
 * <p>
 * The command service returns domain-oriented success or failure values. This assembler
 * is the REST boundary component responsible for translating those values into status
 * codes, response bodies and localized {@link ProblemDetail} payloads.
 *
 * @since 1.0
 */
public final class ResponseEntityFromTechnicalServiceRequestCommandResultAssembler {
    private ResponseEntityFromTechnicalServiceRequestCommandResultAssembler() {
    }

    /**
     * Converts the result of a create technical service request command into an HTTP response.
     *
     * @param result command result returned by the application service
     * @param messageSource message source used to resolve localized failure messages
     * @return {@code 201 Created} with the created request, or a problem response for failures
     */
    public static ResponseEntity<?> toResponseEntityFromCreateResult(
            Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                request -> new ResponseEntity<>(
                        TechnicalServiceRequestResourceFromEntityAssembler.toResourceFromEntity(request),
                        CREATED
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts the result of an update technical service request command into an HTTP response.
     *
     * @param result command result returned by the application service
     * @param messageSource message source used to resolve localized failure messages
     * @return {@code 200 OK} with the updated request, or a problem response for failures
     */
    public static ResponseEntity<?> toResponseEntityFromUpdateResult(
            Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                request -> ResponseEntity.ok(
                        TechnicalServiceRequestResourceFromEntityAssembler.toResourceFromEntity(request)
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts a command failure into a localized {@link ProblemDetail} response.
     *
     * @param failure domain failure to expose through REST
     * @param messageSource message source used to resolve failure details
     * @return response entity with the status code that corresponds to the failure
     */
    private static ResponseEntity<?> toFailureResponse(
            TechnicalServiceRequestCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        var detail = messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    /**
     * Maps command failures to the HTTP status that best represents each business error.
     *
     * @param failure failure returned by the command application service
     * @return HTTP status for the failure
     */
    private static HttpStatus statusFromFailure(TechnicalServiceRequestCommandFailure failure) {
        if (failure instanceof TechnicalServiceRequestCommandFailure.InvalidTransition) {
            return HttpStatus.CONFLICT;
        }
        if (failure instanceof TechnicalServiceRequestCommandFailure.InconsistentIncidentReference) {
            return HttpStatus.BAD_REQUEST;
        }
        if (failure instanceof TechnicalServiceRequestCommandFailure.OrganizationNotFound ||
                failure instanceof TechnicalServiceRequestCommandFailure.AssetNotFound ||
                failure instanceof TechnicalServiceRequestCommandFailure.IncidentNotFound ||
                failure instanceof TechnicalServiceRequestCommandFailure.RequestNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }
}

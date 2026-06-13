package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.application.queryservices.TechnicalServiceRequestQueryFailure;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Assembler that converts technical service request query results into HTTP responses.
 * <p>
 * Query services return domain objects or explicit query failures. This assembler keeps
 * the controller thin by translating those outcomes into REST resources and localized
 * problem payloads.
 *
 * @since 1.0
 */
public final class ResponseEntityFromTechnicalServiceRequestQueryResultAssembler {
    private ResponseEntityFromTechnicalServiceRequestQueryResultAssembler() {
    }

    /**
     * Converts a list query result into an HTTP response.
     *
     * @param result query result returned by the application service
     * @param messageSource message source used to resolve localized failure messages
     * @return {@code 200 OK} with request resources, or a problem response for failures
     */
    public static ResponseEntity<?> toResponseEntityFromListResult(
            Result<List<TechnicalServiceRequest>, TechnicalServiceRequestQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                requests -> ResponseEntity.ok(requests.stream()
                        .map(TechnicalServiceRequestResourceFromEntityAssembler::toResourceFromEntity)
                        .toList()),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts a single request query result into an HTTP response.
     *
     * @param result query result returned by the application service
     * @param messageSource message source used to resolve localized failure messages
     * @return {@code 200 OK} with the request resource, or a problem response for failures
     */
    public static ResponseEntity<?> toResponseEntityFromRequestResult(
            Result<TechnicalServiceRequest, TechnicalServiceRequestQueryFailure> result,
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
     * Converts a query failure into a localized {@link ProblemDetail} response.
     *
     * @param failure query failure to expose through REST
     * @param messageSource message source used to resolve failure details
     * @return response entity with the status code that corresponds to the failure
     */
    private static ResponseEntity<?> toFailureResponse(
            TechnicalServiceRequestQueryFailure failure,
            MessageSource messageSource
    ) {
        var status = HttpStatus.NOT_FOUND;
        var detail = messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }
}

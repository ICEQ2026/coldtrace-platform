package com.acme.coldtrace.platform.reports.interfaces.rest.transform;

import com.acme.coldtrace.platform.reports.application.queryservices.ReportQueryFailure;
import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting report query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromReportQueryResultAssembler {
    /**
     * Converts a report list query result into an HTTP response.
     *
     * @param result query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromListResult(
            Result<List<Report>, ReportQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                reports -> ResponseEntity.ok(reports.stream()
                        .map(ReportResourceFromEntityAssembler::toResourceFromEntity)
                        .toList()),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts a single report query result into an HTTP response.
     *
     * @param result query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromReportResult(
            Result<Report, ReportQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                report -> ResponseEntity.ok(ReportResourceFromEntityAssembler.toResourceFromEntity(report)),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(ReportQueryFailure failure, MessageSource messageSource) {
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

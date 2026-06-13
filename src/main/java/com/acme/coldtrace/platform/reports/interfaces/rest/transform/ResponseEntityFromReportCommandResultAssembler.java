package com.acme.coldtrace.platform.reports.interfaces.rest.transform;

import com.acme.coldtrace.platform.reports.application.commandservices.ReportCommandFailure;
import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Interface layer translator converting report command results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromReportCommandResultAssembler {
    /**
     * Converts a report generation result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 201 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromGenerationResult(
            Result<Report, ReportCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                report -> new ResponseEntity<>(ReportResourceFromEntityAssembler.toResourceFromEntity(report), CREATED),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(ReportCommandFailure failure, MessageSource messageSource) {
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

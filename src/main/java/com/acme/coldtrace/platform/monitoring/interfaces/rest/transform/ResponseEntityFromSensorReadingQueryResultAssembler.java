package com.acme.coldtrace.platform.monitoring.interfaces.rest.transform;

import com.acme.coldtrace.platform.monitoring.application.queryservices.SensorReadingQueryFailure;
import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting sensor reading query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromSensorReadingQueryResultAssembler {
    /**
     * Converts a reading list query result into an HTTP response.
     *
     * @param result query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromListResult(
            Result<List<SensorReading>, SensorReadingQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                readings -> ResponseEntity.ok(readings.stream()
                        .map(SensorReadingResourceFromEntityAssembler::toResourceFromEntity)
                        .toList()),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts a single reading query result into an HTTP response.
     *
     * @param result query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromReadingResult(
            Result<SensorReading, SensorReadingQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                reading -> ResponseEntity.ok(SensorReadingResourceFromEntityAssembler.toResourceFromEntity(reading)),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            SensorReadingQueryFailure failure,
            MessageSource messageSource
    ) {
        var status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                localizeMessage(messageSource, failure)
        ));
    }

    private static String localizeMessage(MessageSource messageSource, SensorReadingQueryFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}

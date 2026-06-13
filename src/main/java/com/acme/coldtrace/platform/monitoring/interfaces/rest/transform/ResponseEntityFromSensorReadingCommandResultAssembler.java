package com.acme.coldtrace.platform.monitoring.interfaces.rest.transform;

import com.acme.coldtrace.platform.monitoring.application.commandservices.SensorReadingCommandFailure;
import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * Interface layer translator converting sensor reading command results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromSensorReadingCommandResultAssembler {
    /**
     * Converts a sensor reading creation result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 201 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromCreateResult(
            Result<SensorReading, SensorReadingCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                reading -> new ResponseEntity<>(
                        SensorReadingResourceFromEntityAssembler.toResourceFromEntity(reading),
                        CREATED
                ),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    /**
     * Converts a demo generation result into an HTTP response.
     *
     * @param result command result
     * @param messageSource message source for localized failure details
     * @return 201 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromGenerationResult(
            Result<List<SensorReading>, SensorReadingCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                readings -> new ResponseEntity<>(readings.stream()
                        .map(SensorReadingResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(), CREATED),
                failure -> toFailureResponse(failure, messageSource)
        );
    }

    private static ResponseEntity<?> toFailureResponse(
            SensorReadingCommandFailure failure,
            MessageSource messageSource
    ) {
        var status = statusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                localizeMessage(messageSource, failure)
        ));
    }

    private static HttpStatus statusFromFailure(SensorReadingCommandFailure failure) {
        if (failure instanceof SensorReadingCommandFailure.IncompatibleLocation ||
                failure instanceof SensorReadingCommandFailure.DeviceNotAssignedToAsset ||
                failure instanceof SensorReadingCommandFailure.DeviceOffline ||
                failure instanceof SensorReadingCommandFailure.GatewayOffline ||
                failure instanceof SensorReadingCommandFailure.AssetSettingsNotFound ||
                failure instanceof SensorReadingCommandFailure.UnsupportedMeasurement ||
                failure instanceof SensorReadingCommandFailure.NoEligibleDevices) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.NOT_FOUND;
    }

    private static String localizeMessage(MessageSource messageSource, SensorReadingCommandFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}

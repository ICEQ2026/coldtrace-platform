package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.IoTDevice;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting IoT device query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromIoTDeviceQueryResultAssembler {
    /**
     * Converts a single IoT device aggregate into a 200 response.
     *
     * @param device device to map
     * @return 200 response with resource body
     */
    public static ResponseEntity<?> toResponseEntityFromIoTDevice(IoTDevice device) {
        return ResponseEntity.ok(IoTDeviceResourceFromEntityAssembler.toResourceFromEntity(device));
    }

    /**
     * Converts a device list into a 200 response.
     *
     * @param devices device list query result
     * @return 200 response with resource list body
     */
    public static ResponseEntity<?> toResponseEntityFromList(List<IoTDevice> devices) {
        var resources = devices.stream()
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Builds a localized 404-response body.
     *
     * @param messageSource source for localized messages
     * @param messageKey message key to resolve
     * @return 404 response containing localized ProblemDetail
     */
    public static ResponseEntity<ProblemDetail> notFound(MessageSource messageSource, String messageKey) {
        var detail = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail));
    }
}

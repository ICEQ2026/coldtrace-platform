package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting asset settings query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromAssetSettingsQueryResultAssembler {
    /**
     * Converts a single asset settings aggregate into a 200 response.
     *
     * @param settings settings to map
     * @return 200 response with resource body
     */
    public static ResponseEntity<?> toResponseEntityFromAssetSettings(AssetSettings settings) {
        return ResponseEntity.ok(AssetSettingsResourceFromEntityAssembler.toResourceFromEntity(settings));
    }

    /**
     * Converts a settings list into a 200 response.
     *
     * @param settings settings list query result
     * @return 200 response with resource list body
     */
    public static ResponseEntity<?> toResponseEntityFromList(List<AssetSettings> settings) {
        var resources = settings.stream()
                .map(AssetSettingsResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    private static ResponseEntity<ProblemDetail> problemDetail(
            HttpStatus status,
            MessageSource messageSource,
            String messageKey,
            Object... args
    ) {
        var detail = messageSource.getMessage(messageKey, args, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    /**
     * Builds a localized 404-response body.
     *
     * @param messageSource source for localized messages
     * @param messageKey message key to resolve
     * @param args optional interpolation values
     * @return 404 response containing localized ProblemDetail
     */
    public static ResponseEntity<ProblemDetail> notFound(
            MessageSource messageSource,
            String messageKey,
            Object... args
    ) {
        return problemDetail(HttpStatus.NOT_FOUND, messageSource, messageKey, args);
    }
}

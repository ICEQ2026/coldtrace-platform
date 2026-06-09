package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting asset query results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromAssetQueryResultAssembler {
    /**
     * Converts a single asset into a 200 response.
     *
     * @param asset asset to map
     * @return 200 response with resource body
     */
    public static ResponseEntity<?> toResponseEntityFromAsset(Asset asset) {
        return ResponseEntity.ok(AssetResourceFromEntityAssembler.toResourceFromEntity(asset));
    }

    /**
     * Converts a list query result into a 200 response.
     *
     * @param assets asset list query result
     * @return 200 response with resource list body
     */
    public static ResponseEntity<?> toResponseEntityFromList(List<Asset> assets) {
        var assetResources = assets.stream()
                .map(AssetResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(assetResources);
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
     * Builds a localized 400-response body.
     *
     * @param messageSource source for localized messages
     * @param messageKey message key to resolve
     * @param args optional interpolation values for localized messages
     * @return 400 response containing localized {@link ProblemDetail}
     */
    public static ResponseEntity<ProblemDetail> badRequest(
            MessageSource messageSource,
            String messageKey,
            Object... args
    ) {
        return problemDetail(HttpStatus.BAD_REQUEST, messageSource, messageKey, args);
    }

    /**
     * Builds a localized 404-response body.
     *
     * @param messageSource source for localized messages
     * @param messageKey message key to resolve
     * @param args optional interpolation values for localized messages
     * @return 404 response containing localized {@link ProblemDetail}
     */
    public static ResponseEntity<ProblemDetail> notFound(
            MessageSource messageSource,
            String messageKey,
            Object... args
    ) {
        return problemDetail(HttpStatus.NOT_FOUND, messageSource, messageKey, args);
    }
}

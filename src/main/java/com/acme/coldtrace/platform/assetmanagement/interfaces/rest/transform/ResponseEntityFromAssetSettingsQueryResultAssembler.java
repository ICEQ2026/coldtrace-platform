package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.application.queryservices.AssetSettingsQueryFailure;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface layer translator converting asset settings query results to HTTP responses.
 * <p>
 * This assembler keeps REST response construction out of the controller and maps
 * application query failures to localized {@link ProblemDetail} responses. The
 * translation preserves the application-layer distinction between a missing asset
 * and missing effective settings while exposing both as HTTP 404 responses.
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
     * Converts a single asset settings query result into an HTTP response.
     * <p>
     * A successful result is transformed into an {@link AssetSettingsResource}. A
     * failure is converted into a localized problem detail using the failure message
     * key supplied by the application layer.
     *
     * @param result asset settings query result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromAssetSettingsResult(
            Result<AssetSettings, AssetSettingsQueryFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                ResponseEntityFromAssetSettingsQueryResultAssembler::toResponseEntityFromAssetSettings,
                failure -> toFailureResponse(failure, messageSource)
        );
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

    private static ResponseEntity<ProblemDetail> toFailureResponse(
            AssetSettingsQueryFailure failure,
            MessageSource messageSource
    ) {
        var status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                status,
                localizeMessage(messageSource, failure)
        ));
    }

    private static String localizeMessage(MessageSource messageSource, AssetSettingsQueryFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}

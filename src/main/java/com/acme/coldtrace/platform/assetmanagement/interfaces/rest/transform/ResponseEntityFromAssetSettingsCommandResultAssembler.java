package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.application.commandservices.AssetSettingsCommandFailure;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Interface layer translator converting asset settings command results to HTTP responses.
 *
 * @since 1.0
 */
public class ResponseEntityFromAssetSettingsCommandResultAssembler {
    /**
     * Converts an asset settings save result into an HTTP response.
     *
     * @param result asset settings command result
     * @param messageSource message source for localized failure details
     * @return 200 response on success or error response on failure
     */
    public static ResponseEntity<?> toResponseEntityFromSaveResult(
            Result<AssetSettings, AssetSettingsCommandFailure> result,
            MessageSource messageSource
    ) {
        return result.fold(
                settings -> ResponseEntity.ok(AssetSettingsResourceFromEntityAssembler.toResourceFromEntity(settings)),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizeMessage(messageSource, failure)
                    ));
                }
        );
    }

    private static HttpStatus statusFromFailure(AssetSettingsCommandFailure failure) {
        if (failure instanceof AssetSettingsCommandFailure.OrganizationNotFound ||
                failure instanceof AssetSettingsCommandFailure.AssetNotFound) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private static String localizeMessage(MessageSource messageSource, AssetSettingsCommandFailure failure) {
        return messageSource.getMessage(
                failure.messageKey(),
                failure.args(),
                failure.messageKey(),
                LocaleContextHolder.getLocale()
        );
    }
}

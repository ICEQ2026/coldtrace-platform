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

public final class ResponseEntityFromTechnicalServiceRequestQueryResultAssembler {
    private ResponseEntityFromTechnicalServiceRequestQueryResultAssembler() {
    }

    public static ResponseEntity<?> toResponseEntityFromListResult(
            Result<List<TechnicalServiceRequest>, TechnicalServiceRequestQueryFailure> result, MessageSource messageSource) {
        return result.fold(
                requests -> ResponseEntity.ok(requests.stream()
                        .map(TechnicalServiceRequestResourceFromEntityAssembler::toResourceFromEntity)
                        .toList()),
                failure -> toFailureResponse(failure, messageSource));
    }

    public static ResponseEntity<?> toResponseEntityFromRequestResult(
            Result<TechnicalServiceRequest, TechnicalServiceRequestQueryFailure> result, MessageSource messageSource) {
        return result.fold(
                request -> ResponseEntity.ok(TechnicalServiceRequestResourceFromEntityAssembler.toResourceFromEntity(request)),
                failure -> toFailureResponse(failure, messageSource));
    }

    private static ResponseEntity<?> toFailureResponse(TechnicalServiceRequestQueryFailure failure, MessageSource messageSource) {
        var status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status,
                messageSource.getMessage(failure.messageKey(), failure.args(), failure.messageKey(), LocaleContextHolder.getLocale())));
    }
}

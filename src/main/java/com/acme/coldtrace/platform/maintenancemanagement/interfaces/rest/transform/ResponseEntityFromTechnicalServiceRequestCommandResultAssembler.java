package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.maintenancemanagement.application.commandservices.TechnicalServiceRequestCommandFailure;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.HttpStatus.CREATED;

public final class ResponseEntityFromTechnicalServiceRequestCommandResultAssembler {
    private ResponseEntityFromTechnicalServiceRequestCommandResultAssembler() {
    }

    public static ResponseEntity<?> toResponseEntityFromCreateResult(
            Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> result, MessageSource messageSource) {
        return result.fold(
                request -> new ResponseEntity<>(TechnicalServiceRequestResourceFromEntityAssembler.toResourceFromEntity(request), CREATED),
                failure -> toFailureResponse(failure, messageSource));
    }

    public static ResponseEntity<?> toResponseEntityFromUpdateResult(
            Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> result, MessageSource messageSource) {
        return result.fold(
                request -> ResponseEntity.ok(TechnicalServiceRequestResourceFromEntityAssembler.toResourceFromEntity(request)),
                failure -> toFailureResponse(failure, messageSource));
    }

    private static ResponseEntity<?> toFailureResponse(TechnicalServiceRequestCommandFailure failure, MessageSource messageSource) {
        var status = statusFromFailure(failure);
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status,
                messageSource.getMessage(failure.messageKey(), failure.args(), failure.messageKey(), LocaleContextHolder.getLocale())));
    }

    private static HttpStatus statusFromFailure(TechnicalServiceRequestCommandFailure failure) {
        if (failure instanceof TechnicalServiceRequestCommandFailure.InvalidTransition) return HttpStatus.CONFLICT;
        if (failure instanceof TechnicalServiceRequestCommandFailure.OrganizationNotFound ||
                failure instanceof TechnicalServiceRequestCommandFailure.AssetNotFound ||
                failure instanceof TechnicalServiceRequestCommandFailure.IncidentNotFound ||
                failure instanceof TechnicalServiceRequestCommandFailure.RequestNotFound) return HttpStatus.NOT_FOUND;
        return HttpStatus.BAD_REQUEST;
    }
}

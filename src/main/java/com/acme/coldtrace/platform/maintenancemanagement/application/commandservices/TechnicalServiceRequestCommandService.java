package com.acme.coldtrace.platform.maintenancemanagement.application.commandservices;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.TechnicalServiceRequest;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateTechnicalServiceRequestCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateTechnicalServiceRequestStatusCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

public interface TechnicalServiceRequestCommandService {
    Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> handle(CreateTechnicalServiceRequestCommand command);
    Result<TechnicalServiceRequest, TechnicalServiceRequestCommandFailure> handle(UpdateTechnicalServiceRequestStatusCommand command);
}

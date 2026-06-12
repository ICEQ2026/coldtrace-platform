package com.acme.coldtrace.platform.maintenancemanagement.application.internal.eventhandlers;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.events.TechnicalServiceRequestCreatedEvent;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.events.TechnicalServiceRequestCreatedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Internal handler that translates technical service request domain events to integration events.
 *
 * @since 1.0
 */
@Service
public class TechnicalServiceRequestCreatedEventHandler {
    private final ApplicationEventPublisher eventPublisher;

    public TechnicalServiceRequestCreatedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the public integration event for a created technical service request.
     *
     * @param event internal domain event
     */
    @EventListener
    public void on(TechnicalServiceRequestCreatedEvent event) {
        eventPublisher.publishEvent(new TechnicalServiceRequestCreatedIntegrationEvent(
                event.technicalServiceRequestId(),
                event.organizationId(),
                event.assetId(),
                event.code(),
                event.requestedAt()
        ));
    }
}

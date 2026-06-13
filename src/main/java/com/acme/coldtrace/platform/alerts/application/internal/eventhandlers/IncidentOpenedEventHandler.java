package com.acme.coldtrace.platform.alerts.application.internal.eventhandlers;

import com.acme.coldtrace.platform.alerts.domain.model.events.IncidentOpenedEvent;
import com.acme.coldtrace.platform.alerts.interfaces.events.IncidentOpenedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Internal handler that translates alert domain events to integration events.
 *
 * @since 1.0
 */
@Service
public class IncidentOpenedEventHandler {
    private final ApplicationEventPublisher eventPublisher;

    public IncidentOpenedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the public integration event for an opened incident.
     *
     * @param event internal domain event
     */
    @EventListener
    public void on(IncidentOpenedEvent event) {
        eventPublisher.publishEvent(new IncidentOpenedIntegrationEvent(
                event.incidentId(),
                event.organizationId(),
                event.assetId(),
                event.severity(),
                event.detectedAt()
        ));
    }
}

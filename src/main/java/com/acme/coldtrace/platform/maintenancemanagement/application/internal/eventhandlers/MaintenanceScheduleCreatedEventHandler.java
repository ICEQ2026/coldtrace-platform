package com.acme.coldtrace.platform.maintenancemanagement.application.internal.eventhandlers;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.events.MaintenanceScheduleCreatedEvent;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.events.MaintenanceScheduleCreatedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Internal handler that translates maintenance schedule domain events to integration events.
 *
 * @since 1.0
 */
@Service
public class MaintenanceScheduleCreatedEventHandler {
    private final ApplicationEventPublisher eventPublisher;

    public MaintenanceScheduleCreatedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the public integration event for a created maintenance schedule.
     *
     * @param event internal domain event
     */
    @EventListener
    public void on(MaintenanceScheduleCreatedEvent event) {
        eventPublisher.publishEvent(new MaintenanceScheduleCreatedIntegrationEvent(
                event.maintenanceScheduleId(),
                event.organizationId(),
                event.assetId(),
                event.uuid(),
                event.scheduledDate()
        ));
    }
}

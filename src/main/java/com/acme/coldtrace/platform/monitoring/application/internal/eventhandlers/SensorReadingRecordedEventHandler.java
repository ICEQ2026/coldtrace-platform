package com.acme.coldtrace.platform.monitoring.application.internal.eventhandlers;

import com.acme.coldtrace.platform.monitoring.domain.model.events.SensorReadingRecordedEvent;
import com.acme.coldtrace.platform.monitoring.interfaces.events.SensorReadingRecordedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Internal handler that translates monitoring domain events to integration events.
 *
 * @since 1.0
 */
@Service
public class SensorReadingRecordedEventHandler {
    private final ApplicationEventPublisher eventPublisher;

    public SensorReadingRecordedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the public integration event for recorded telemetry.
     *
     * @param event internal domain event
     */
    @EventListener
    public void on(SensorReadingRecordedEvent event) {
        eventPublisher.publishEvent(new SensorReadingRecordedIntegrationEvent(
                event.sensorReadingId(),
                event.organizationId(),
                event.assetId(),
                event.iotDeviceId(),
                event.outOfRange(),
                event.recordedAt()
        ));
    }
}

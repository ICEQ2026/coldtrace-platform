package com.acme.coldtrace.platform.assetmanagement.application.internal.eventhandlers;

import com.acme.coldtrace.platform.assetmanagement.domain.model.events.AssetCreatedEvent;
import com.acme.coldtrace.platform.assetmanagement.interfaces.events.AssetCreatedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Internal handler that translates asset domain events to integration events.
 *
 * @since 1.0
 */
@Service
public class AssetCreatedEventHandler {
    private final ApplicationEventPublisher eventPublisher;

    public AssetCreatedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the public integration event for an asset-created domain event.
     *
     * @param event internal domain event
     */
    @EventListener
    public void on(AssetCreatedEvent event) {
        eventPublisher.publishEvent(new AssetCreatedIntegrationEvent(
                event.assetId(),
                event.organizationId(),
                event.locationId(),
                event.uuid(),
                event.name()
        ));
    }
}

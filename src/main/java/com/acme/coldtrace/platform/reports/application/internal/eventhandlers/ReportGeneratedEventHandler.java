package com.acme.coldtrace.platform.reports.application.internal.eventhandlers;

import com.acme.coldtrace.platform.reports.domain.model.events.ReportGeneratedEvent;
import com.acme.coldtrace.platform.reports.interfaces.events.ReportGeneratedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Internal handler that translates reports domain events to integration events.
 *
 * @since 1.0
 */
@Service
public class ReportGeneratedEventHandler {
    private final ApplicationEventPublisher eventPublisher;

    public ReportGeneratedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the public integration event for a generated report.
     *
     * @param event internal domain event
     */
    @EventListener
    public void on(ReportGeneratedEvent event) {
        eventPublisher.publishEvent(new ReportGeneratedIntegrationEvent(
                event.reportId(),
                event.organizationId(),
                event.uuid(),
                event.type(),
                event.generatedAt()
        ));
    }
}

package com.acme.coldtrace.platform.monitoring.application.acl;

import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application-layer implementation of {@link MonitoringContextFacade}.
 *
 * @since 1.0
 */
@Service
public class MonitoringContextFacadeImpl implements MonitoringContextFacade {
    private final SensorReadingRepository sensorReadingRepository;

    public MonitoringContextFacadeImpl(SensorReadingRepository sensorReadingRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SensorReadingSnapshot> fetchSensorReadingsByOrganizationId(Long organizationId) {
        return sensorReadingRepository.findAllByOrganizationId(organizationId).stream()
                .map(this::toSnapshot)
                .toList();
    }

    private SensorReadingSnapshot toSnapshot(SensorReading reading) {
        return new SensorReadingSnapshot(
                reading.getId(),
                reading.getOrganizationId(),
                reading.getAssetId(),
                reading.getTemperature(),
                reading.getHumidity(),
                reading.getOutOfRange(),
                reading.getRecordedAt()
        );
    }
}

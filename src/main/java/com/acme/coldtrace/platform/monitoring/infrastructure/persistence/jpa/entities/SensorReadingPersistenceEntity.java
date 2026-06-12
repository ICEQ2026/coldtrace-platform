package com.acme.coldtrace.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * JPA persistence entity for sensor readings.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "sensor_readings")
public class SensorReadingPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long assetId;

    @Column(nullable = false)
    private Long iotDeviceId;

    @Column(nullable = false)
    private Long gatewayId;

    @Column(nullable = false)
    private Long locationId;

    @Column
    private Double temperature;

    @Column
    private Double humidity;

    @Column(nullable = false)
    private Boolean outOfRange;

    @Column(nullable = false)
    private OffsetDateTime recordedAt;

    @Column
    private Boolean motionDetected;

    @Column
    private Boolean imageCaptured;

    @Column
    private Integer batteryLevel;

    @Column
    private Integer signalStrength;
}

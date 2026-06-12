package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.IoTDevice;
import com.acme.coldtrace.platform.assetmanagement.domain.model.valueobjects.IoTDeviceUuid;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.converters.IoTDeviceUuidPersistenceConverter;
import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for IoT devices.
 * <p>
 * The entity owns persistence-specific collection and column mappings. Domain
 * code works with the pure {@code IoTDevice} aggregate and is rebuilt through
 * the persistence assembler.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "iot_devices", uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"organization_id", "uuid"},
                name = IoTDevice.ORGANIZATION_ID_UUID_UNIQUE_CONSTRAINT
        )
})
public class IoTDevicePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long gatewayId;

    @Convert(converter = IoTDeviceUuidPersistenceConverter.class)
    @Column(nullable = false)
    private IoTDeviceUuid uuid;

    @Column(nullable = false)
    private String deviceType;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String measurementType;

    @ElementCollection
    @CollectionTable(
            name = "iot_device_measurement_parameters",
            joinColumns = @JoinColumn(name = "iot_device_id")
    )
    @Column(name = "measurement_parameter", nullable = false)
    private List<String> measurementParameters = new ArrayList<>();

    @Column(nullable = false)
    private Integer readingFrequencySeconds;

    @Column
    private Long assetId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String calibrationStatus;

    @Column(nullable = false)
    private LocalDate lastCalibrationDate;

    @Column(nullable = false)
    private LocalDate nextCalibrationDate;
}

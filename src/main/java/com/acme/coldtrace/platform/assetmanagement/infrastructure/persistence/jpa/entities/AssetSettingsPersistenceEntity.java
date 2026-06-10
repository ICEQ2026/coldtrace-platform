package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA persistence entity for asset settings records.
 * <p>
 * The entity owns database mapping details such as collection tables and
 * nullable asset references. Domain code works with the pure
 * {@code AssetSettings} aggregate and is rebuilt through the persistence
 * assembler.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "asset_settings")
public class AssetSettingsPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column
    private Long assetId;

    @Column(nullable = false)
    private String uuid;

    @ElementCollection
    @CollectionTable(
            name = "asset_settings_asset_types",
            joinColumns = @JoinColumn(name = "asset_settings_id")
    )
    @Column(name = "asset_type", nullable = false)
    private List<String> assetTypes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "asset_settings_iot_device_types",
            joinColumns = @JoinColumn(name = "asset_settings_id")
    )
    @Column(name = "iot_device_type", nullable = false)
    private List<String> iotDeviceTypes = new ArrayList<>();

    @Column(nullable = false)
    private Double minimumTemperature;

    @Column(nullable = false)
    private Double maximumTemperature;

    @Column(nullable = false)
    private Double minimumHumidity;

    @Column(nullable = false)
    private Double maximumHumidity;

    @Column(nullable = false)
    private Integer calibrationFrequencyDays;

    @Column(nullable = false)
    private String temperatureUnit;

    @Column(nullable = false)
    private String humidityUnit;

    @Column(nullable = false)
    private String weightUnit;

    @Column(nullable = false)
    private Integer readingFrequencySeconds;

    @Column(nullable = false)
    private Integer alertThresholdMinutes;
}

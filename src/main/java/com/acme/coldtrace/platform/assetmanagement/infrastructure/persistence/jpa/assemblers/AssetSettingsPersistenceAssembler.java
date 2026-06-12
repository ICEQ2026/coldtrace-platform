package com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.assemblers;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.entities.AssetSettingsPersistenceEntity;

import java.util.ArrayList;

/**
 * Assembler that translates asset settings between domain and persistence models.
 *
 * @since 1.0
 */
public final class AssetSettingsPersistenceAssembler {
    private AssetSettingsPersistenceAssembler() {
    }

    /**
     * Converts a persistence entity into the domain aggregate.
     *
     * @param entity persistence entity
     * @return asset settings aggregate
     */
    public static AssetSettings toDomainFromPersistence(AssetSettingsPersistenceEntity entity) {
        return new AssetSettings(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getAssetId(),
                entity.getUuid(),
                entity.getAssetTypes(),
                entity.getIotDeviceTypes(),
                entity.getMinimumTemperature(),
                entity.getMaximumTemperature(),
                entity.getMinimumHumidity(),
                entity.getMaximumHumidity(),
                entity.getCalibrationFrequencyDays(),
                entity.getTemperatureUnit(),
                entity.getHumidityUnit(),
                entity.getWeightUnit(),
                entity.getReadingFrequencySeconds(),
                entity.getAlertThresholdMinutes()
        );
    }

    /**
     * Converts a domain aggregate into a new persistence entity.
     *
     * @param assetSettings domain aggregate
     * @return persistence entity
     */
    public static AssetSettingsPersistenceEntity toPersistenceFromDomain(AssetSettings assetSettings) {
        var entity = new AssetSettingsPersistenceEntity();
        entity.setId(assetSettings.getId());
        copyDomainState(assetSettings, entity);
        return entity;
    }

    /**
     * Copies domain state into an existing persistence entity.
     *
     * @param assetSettings source aggregate
     * @param entity target persistence entity
     */
    public static void copyDomainState(AssetSettings assetSettings, AssetSettingsPersistenceEntity entity) {
        entity.setOrganizationId(assetSettings.getOrganizationId());
        entity.setAssetId(assetSettings.getAssetId());
        entity.setUuid(assetSettings.getUuid());
        entity.setAssetTypes(new ArrayList<>(assetSettings.getAssetTypes()));
        entity.setIotDeviceTypes(new ArrayList<>(assetSettings.getIotDeviceTypes()));
        entity.setMinimumTemperature(assetSettings.getMinimumTemperature());
        entity.setMaximumTemperature(assetSettings.getMaximumTemperature());
        entity.setMinimumHumidity(assetSettings.getMinimumHumidity());
        entity.setMaximumHumidity(assetSettings.getMaximumHumidity());
        entity.setCalibrationFrequencyDays(assetSettings.getCalibrationFrequencyDays());
        entity.setTemperatureUnit(assetSettings.getTemperatureUnit());
        entity.setHumidityUnit(assetSettings.getHumidityUnit());
        entity.setWeightUnit(assetSettings.getWeightUnit());
        entity.setReadingFrequencySeconds(assetSettings.getReadingFrequencySeconds());
        entity.setAlertThresholdMinutes(assetSettings.getAlertThresholdMinutes());
    }
}

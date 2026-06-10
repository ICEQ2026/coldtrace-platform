package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.AssetSettingsResource;

/**
 * Assembler that converts asset settings aggregates into REST response resources.
 *
 * @since 1.0
 */
public class AssetSettingsResourceFromEntityAssembler {
    /**
     * Converts an asset settings aggregate into an asset settings resource.
     *
     * @param assetSettings settings aggregate to expose through REST
     * @return asset settings response resource
     */
    public static AssetSettingsResource toResourceFromEntity(AssetSettings assetSettings) {
        return new AssetSettingsResource(
                assetSettings.getId(),
                assetSettings.getOrganizationId(),
                assetSettings.getAssetId(),
                assetSettings.getUuid(),
                assetSettings.getAssetTypes(),
                assetSettings.getIotDeviceTypes(),
                assetSettings.getMinimumTemperature(),
                assetSettings.getMaximumTemperature(),
                assetSettings.getMinimumHumidity(),
                assetSettings.getMaximumHumidity(),
                assetSettings.getCalibrationFrequencyDays(),
                assetSettings.getTemperatureUnit(),
                assetSettings.getHumidityUnit(),
                assetSettings.getWeightUnit(),
                assetSettings.getReadingFrequencySeconds(),
                assetSettings.getAlertThresholdMinutes()
        );
    }
}

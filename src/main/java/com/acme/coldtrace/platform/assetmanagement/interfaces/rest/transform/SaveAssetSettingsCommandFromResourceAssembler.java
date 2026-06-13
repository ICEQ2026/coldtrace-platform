package com.acme.coldtrace.platform.assetmanagement.interfaces.rest.transform;

import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.SaveAssetSettingsCommand;
import com.acme.coldtrace.platform.assetmanagement.interfaces.rest.resources.SaveAssetSettingsResource;

/**
 * Interface layer translator converting asset settings resources to commands.
 *
 * @since 1.0
 */
public class SaveAssetSettingsCommandFromResourceAssembler {
    /**
     * Converts a request resource to a SaveAssetSettingsCommand.
     *
     * @param resource asset settings request resource
     * @param organizationId organization identifier from the route
     * @param assetId optional asset identifier from the route
     * @return save asset settings command
     */
    public static SaveAssetSettingsCommand toCommandFromResource(
            SaveAssetSettingsResource resource,
            Long organizationId,
            Long assetId
    ) {
        return new SaveAssetSettingsCommand(
                organizationId,
                assetId,
                resource.uuid(),
                resource.assetTypes(),
                resource.iotDeviceTypes(),
                resource.minimumTemperature(),
                resource.maximumTemperature(),
                resource.minimumHumidity(),
                resource.maximumHumidity(),
                resource.calibrationFrequencyDays(),
                resource.temperatureUnit(),
                resource.humidityUnit(),
                resource.weightUnit(),
                resource.readingFrequencySeconds(),
                resource.alertThresholdMinutes()
        );
    }
}

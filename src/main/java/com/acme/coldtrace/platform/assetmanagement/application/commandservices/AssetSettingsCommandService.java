package com.acme.coldtrace.platform.assetmanagement.application.commandservices;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.domain.model.commands.SaveAssetSettingsCommand;
import com.acme.coldtrace.platform.shared.application.result.Result;

/**
 * Application service contract for asset settings command operations.
 *
 * @since 1.0
 */
public interface AssetSettingsCommandService {
    /**
     * Handles creation or update of asset settings.
     *
     * @param command command containing threshold and operational settings
     * @return success with saved settings or failure with a command error
     * @throws IllegalArgumentException if the command contains invalid settings data
     * @see SaveAssetSettingsCommand
     */
    Result<AssetSettings, AssetSettingsCommandFailure> handle(SaveAssetSettingsCommand command);
}

package com.acme.coldtrace.platform.assetmanagement.application.commandservices;

/**
 * Failure types for asset settings command execution.
 *
 * @since 1.0
 */
public sealed interface AssetSettingsCommandFailure
        permits AssetSettingsCommandFailure.OrganizationNotFound,
        AssetSettingsCommandFailure.AssetNotFound {
    /**
     * Returns the message key associated with the failure.
     *
     * @return message key to resolve through i18n
     */
    String messageKey();

    /**
     * Returns optional arguments for message interpolation.
     *
     * @return message interpolation arguments
     */
    default Object[] args() {
        return new Object[0];
    }

    /**
     * Organization not found failure.
     */
    record OrganizationNotFound() implements AssetSettingsCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.asset-settings.error.organization-not-found";
        }
    }

    /**
     * Asset not found failure.
     */
    record AssetNotFound() implements AssetSettingsCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.asset-settings.error.asset-not-found";
        }
    }
}

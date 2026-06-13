package com.acme.coldtrace.platform.assetmanagement.application.queryservices;

/**
 * Failure types returned by asset settings query use cases.
 * <p>
 * These failures make read-side validation explicit when resolving the effective
 * safety and telemetry thresholds for an asset. The monitoring bounded context
 * depends on this distinction to know whether the requested asset does not
 * belong to the organization or whether the asset exists but still has no
 * asset-specific nor organization-default settings configured.
 *
 * @since 1.0
 */
public sealed interface AssetSettingsQueryFailure
        permits AssetSettingsQueryFailure.AssetNotFound,
        AssetSettingsQueryFailure.AssetSettingsNotFound {
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
     * Indicates that the requested asset could not be found inside the requested organization.
     * <p>
     * This failure protects organization ownership boundaries and prevents callers from
     * treating a cross-organization asset identifier as a missing configuration.
     *
     * @since 1.0
     */
    record AssetNotFound() implements AssetSettingsQueryFailure {
        @Override
        public String messageKey() {
            return "asset-management.asset-settings.error.asset-not-found";
        }
    }

    /**
     * Indicates that the asset exists, but no effective settings can be resolved for it.
     * <p>
     * Effective settings are resolved by first looking for asset-specific settings and
     * then falling back to organization-default settings. This failure is returned only
     * when both lookups are empty.
     *
     * @since 1.0
     */
    record AssetSettingsNotFound() implements AssetSettingsQueryFailure {
        @Override
        public String messageKey() {
            return "asset-management.asset-settings.error.asset-settings-not-found";
        }
    }
}

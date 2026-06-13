package com.acme.coldtrace.platform.assetmanagement.application.internal.queryservices;

import com.acme.coldtrace.platform.assetmanagement.application.queryservices.AssetSettingsQueryFailure;
import com.acme.coldtrace.platform.assetmanagement.application.queryservices.AssetSettingsQueryService;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.AssetSettings;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetAssetSettingsByOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetEffectiveAssetSettingsByAssetIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetRepository;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetSettingsRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementation for asset settings queries.
 * <p>
 * This service coordinates read-side access to asset settings through domain
 * repositories only. It does not expose persistence entities to upper layers and
 * keeps effective settings resolution in the application layer, where ownership
 * validation and fallback rules can be expressed as part of the use case.
 *
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class AssetSettingsQueryServicesImpl implements AssetSettingsQueryService {
    private final AssetSettingsRepository assetSettingsRepository;
    private final AssetRepository assetRepository;

    public AssetSettingsQueryServicesImpl(
            AssetSettingsRepository assetSettingsRepository,
            AssetRepository assetRepository
    ) {
        this.assetSettingsRepository = assetSettingsRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public List<AssetSettings> handle(GetAssetSettingsByOrganizationIdQuery query) {
        return assetSettingsRepository.findAllByOrganizationId(query.organizationId());
    }

    /**
     * Resolves the effective settings for an organization asset.
     * <p>
     * The asset is validated first with the organization identifier to avoid returning
     * defaults for an asset that is missing or belongs to another organization. After
     * ownership is confirmed, asset-specific settings take precedence over the
     * organization default.
     *
     * @param query query containing the organization and asset identifiers
     * @return success with the resolved settings or a failure describing the missing prerequisite
     * @see GetEffectiveAssetSettingsByAssetIdQuery
     */
    @Override
    public Result<AssetSettings, AssetSettingsQueryFailure> handle(GetEffectiveAssetSettingsByAssetIdQuery query) {
        if (assetRepository.findByIdAndOrganizationId(query.assetId(), query.organizationId()).isEmpty()) {
            return Result.failure(new AssetSettingsQueryFailure.AssetNotFound());
        }
        var settings = assetSettingsRepository.findByOrganizationIdAndAssetId(query.organizationId(), query.assetId())
                .or(() -> assetSettingsRepository.findDefaultByOrganizationId(query.organizationId()));
        return settings
                .<Result<AssetSettings, AssetSettingsQueryFailure>>map(Result::success)
                .orElseGet(() -> Result.failure(new AssetSettingsQueryFailure.AssetSettingsNotFound()));
    }
}

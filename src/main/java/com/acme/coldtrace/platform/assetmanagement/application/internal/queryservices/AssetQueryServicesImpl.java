package com.acme.coldtrace.platform.assetmanagement.application.internal.queryservices;

import com.acme.coldtrace.platform.assetmanagement.application.queryservices.AssetQueryService;
import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetAssetByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetAssetsByOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.infrastructure.persistence.jpa.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementation for asset query operations.
 * <p>
 * The implementation delegates to the asset repository and keeps query access
 * read-only. Each operation preserves the organization scope required by the
 * route design and by the bounded context.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class AssetQueryServicesImpl implements AssetQueryService {
    private final AssetRepository assetRepository;

    public AssetQueryServicesImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    /**
     * Retrieves assets from persistence by organization.
     *
     * @param query query object containing the organization identifier
     * @return assets for the organization, possibly empty
     * @see GetAssetsByOrganizationIdQuery
     */
    @Override
    public List<Asset> handle(GetAssetsByOrganizationIdQuery query) {
        log.debug("Querying assets by organizationId={}", query.organizationId());
        var assets = assetRepository.findAllByOrganizationId(query.organizationId());
        log.debug("Found {} assets for organizationId={}", assets.size(), query.organizationId());
        return assets;
    }

    /**
     * Retrieves one asset from persistence by id and organization.
     *
     * @param query query object containing organization and asset identifiers
     * @return asset when found, otherwise empty
     * @see GetAssetByIdAndOrganizationIdQuery
     */
    @Override
    public Optional<Asset> handle(GetAssetByIdAndOrganizationIdQuery query) {
        log.debug("Querying asset by organizationId={}, assetId={}",
                query.organizationId(), query.assetId());
        var asset = assetRepository.findByIdAndOrganizationId(query.assetId(), query.organizationId());
        if (asset.isEmpty()) {
            log.warn("Asset not found: organizationId={}, assetId={}",
                    query.organizationId(), query.assetId());
        }
        return asset;
    }
}

package com.acme.coldtrace.platform.assetmanagement.application.queryservices;

import com.acme.coldtrace.platform.assetmanagement.domain.model.aggregates.Asset;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetAssetByIdAndOrganizationIdQuery;
import com.acme.coldtrace.platform.assetmanagement.domain.model.queries.GetAssetsByOrganizationIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for asset query use cases.
 * <p>
 * Query services expose read-only operations used by the REST interface. They
 * preserve the organization boundary by requiring the organization identifier
 * in every query instead of exposing global asset lookups.
 *
 * @since 1.0
 */
public interface AssetQueryService {
    /**
     * Retrieves all assets that belong to an organization.
     *
     * @param query query object containing the organization identifier
     * @return organization assets, possibly empty
     * @see GetAssetsByOrganizationIdQuery
     */
    List<Asset> handle(GetAssetsByOrganizationIdQuery query);

    /**
     * Retrieves one asset by id and organization.
     *
     * @param query query object containing organization and asset identifiers
     * @return asset when found, otherwise empty
     * @see GetAssetByIdAndOrganizationIdQuery
     */
    Optional<Asset> handle(GetAssetByIdAndOrganizationIdQuery query);
}

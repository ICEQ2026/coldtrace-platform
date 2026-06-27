package com.acme.coldtrace.platform.billing.application.internal.services;

import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.billing.application.model.OrganizationSubscriptionUsage;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

/**
 * Builds organization usage snapshots for subscription entitlement evaluation.
 *
 * @since 1.0
 */
@Service
public class OrganizationSubscriptionUsageService {
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final IamContextFacade iamContextFacade;

    public OrganizationSubscriptionUsageService(
            AssetManagementContextFacade assetManagementContextFacade,
            IamContextFacade iamContextFacade
    ) {
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Counts supported resources for the organization.
     *
     * @param organizationId organization identifier
     * @return current usage snapshot
     */
    public OrganizationSubscriptionUsage snapshotFor(Long organizationId) {
        return new OrganizationSubscriptionUsage(
                assetManagementContextFacade.countLocationsByOrganizationId(organizationId),
                assetManagementContextFacade.countAssetsByOrganizationId(organizationId),
                assetManagementContextFacade.countIotDevicesByOrganizationId(organizationId),
                iamContextFacade.countUsersByOrganizationId(organizationId)
        );
    }
}

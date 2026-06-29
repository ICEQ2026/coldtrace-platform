package com.acme.coldtrace.platform.assetmanagement.application.commandservices;

import com.acme.coldtrace.platform.billing.interfaces.acl.PlanEntitlementFailure;
import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;

/**
 * Failure types for IoT device command execution.
 *
 * @since 1.0
 */
public sealed interface IoTDeviceCommandFailure
        permits IoTDeviceCommandFailure.OrganizationNotFound,
        IoTDeviceCommandFailure.GatewayNotFound,
        IoTDeviceCommandFailure.AssetNotFound,
        IoTDeviceCommandFailure.IncompatibleAssetLocation,
        IoTDeviceCommandFailure.IoTDeviceNotFound,
        IoTDeviceCommandFailure.DuplicateUuid,
        IoTDeviceCommandFailure.PlanLimitExceeded {
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

    /** Organization not found failure. */
    record OrganizationNotFound() implements IoTDeviceCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.iot-device.error.organization-not-found";
        }
    }

    /** Gateway not found failure. */
    record GatewayNotFound() implements IoTDeviceCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.iot-device.error.gateway-not-found";
        }
    }

    /** Asset not found failure. */
    record AssetNotFound() implements IoTDeviceCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.iot-device.error.asset-not-found";
        }
    }

    /** Incompatible asset and gateway location failure. */
    record IncompatibleAssetLocation() implements IoTDeviceCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.iot-device.error.incompatible-location";
        }
    }

    /** IoT device not found failure. */
    record IoTDeviceNotFound() implements IoTDeviceCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.iot-device.error.iot-device-not-found";
        }
    }

    /** Duplicate uuid failure. */
    record DuplicateUuid() implements IoTDeviceCommandFailure {
        @Override
        public String messageKey() {
            return "asset-management.iot-device.error.uuid.duplicate";
        }
    }

    /** Plan limit exceeded failure. */
    record PlanLimitExceeded(SubscriptionBillingContextFacade.EntitlementCheckSnapshot entitlement)
            implements IoTDeviceCommandFailure, PlanEntitlementFailure {
        @Override
        public String messageKey() {
            return "asset-management.iot-device.error.plan-limit-exceeded";
        }
    }
}

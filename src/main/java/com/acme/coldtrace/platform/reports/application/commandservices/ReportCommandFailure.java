package com.acme.coldtrace.platform.reports.application.commandservices;

import com.acme.coldtrace.platform.billing.interfaces.acl.PlanEntitlementFailure;
import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;

/**
 * Failure types for report command execution.
 *
 * @since 1.0
 */
public sealed interface ReportCommandFailure
        permits ReportCommandFailure.OrganizationNotFound, ReportCommandFailure.PlanLimitExceeded {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements ReportCommandFailure {
        @Override
        public String messageKey() {
            return "reports.report.error.organization-not-found";
        }
    }

    /** Plan entitlement failure. */
    record PlanLimitExceeded(SubscriptionBillingContextFacade.EntitlementCheckSnapshot entitlement)
            implements ReportCommandFailure, PlanEntitlementFailure {
        @Override
        public String messageKey() {
            return "reports.report.error.plan-limit-exceeded";
        }
    }
}

package com.acme.coldtrace.platform.reports.application.commandservices;

/**
 * Failure types for report command execution.
 *
 * @since 1.0
 */
public sealed interface ReportCommandFailure permits ReportCommandFailure.OrganizationNotFound {
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
}

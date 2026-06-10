package com.acme.coldtrace.platform.reports.application.queryservices;

/**
 * Failure types for report query execution.
 *
 * @since 1.0
 */
public sealed interface ReportQueryFailure
        permits ReportQueryFailure.OrganizationNotFound, ReportQueryFailure.ReportNotFound {
    /** @return message key to resolve through i18n */
    String messageKey();

    /** @return optional message interpolation arguments */
    default Object[] args() {
        return new Object[0];
    }

    /** Organization not found failure. */
    record OrganizationNotFound() implements ReportQueryFailure {
        @Override
        public String messageKey() {
            return "reports.report.error.organization-not-found";
        }
    }

    /** Report not found failure. */
    record ReportNotFound() implements ReportQueryFailure {
        @Override
        public String messageKey() {
            return "reports.report.error.report-not-found";
        }
    }
}

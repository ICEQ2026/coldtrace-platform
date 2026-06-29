package com.acme.coldtrace.platform.reports.application.internal.commandservices;

import com.acme.coldtrace.platform.alerts.interfaces.acl.AlertsContextFacade;
import com.acme.coldtrace.platform.alerts.interfaces.acl.AlertsContextFacade.IncidentSnapshot;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade.SensorReadingSnapshot;
import com.acme.coldtrace.platform.reports.application.commandservices.ReportCommandFailure;
import com.acme.coldtrace.platform.reports.application.commandservices.ReportCommandService;
import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.domain.model.commands.GenerateReportCommand;
import com.acme.coldtrace.platform.reports.domain.repositories.ReportRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade.ENTITLEMENT_REPORT_HISTORY;

/**
 * Application service implementation for report command operations.
 * <p>
 * The service generates reports from persisted operational data instead of
 * static frontend records. It reads monitoring data, incident data and assets
 * through bounded-context repository contracts, derives summary metrics and
 * persists the resulting report snapshot.
 *
 * @since 1.0
 */
@Slf4j
@Service
public class ReportCommandServiceImpl implements ReportCommandService {
    private final ReportRepository reportRepository;
    private final IamContextFacade iamContextFacade;
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final MonitoringContextFacade monitoringContextFacade;
    private final AlertsContextFacade alertsContextFacade;
    private final SubscriptionBillingContextFacade subscriptionBillingContextFacade;

    public ReportCommandServiceImpl(
            ReportRepository reportRepository,
            IamContextFacade iamContextFacade,
            AssetManagementContextFacade assetManagementContextFacade,
            MonitoringContextFacade monitoringContextFacade,
            AlertsContextFacade alertsContextFacade,
            SubscriptionBillingContextFacade subscriptionBillingContextFacade
    ) {
        this.reportRepository = reportRepository;
        this.iamContextFacade = iamContextFacade;
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.monitoringContextFacade = monitoringContextFacade;
        this.alertsContextFacade = alertsContextFacade;
        this.subscriptionBillingContextFacade = subscriptionBillingContextFacade;
    }

    /**
     * Handles report generation for an organization and date range.
     *
     * @param command command containing report scope
     * @return success with generated report or failure with command error
     * @see GenerateReportCommand
     */
    @Override
    @Transactional
    public Result<Report, ReportCommandFailure> handle(GenerateReportCommand command) {
        if (!iamContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for report generation: organizationId={}", command.organizationId());
            return Result.failure(new ReportCommandFailure.OrganizationNotFound());
        }
        var entitlement = subscriptionBillingContextFacade.checkEntitlement(
                command.organizationId(),
                ENTITLEMENT_REPORT_HISTORY
        );
        if (entitlement.isPresent() && !Boolean.TRUE.equals(entitlement.get().enabled())) {
            log.warn("Report generation blocked by plan entitlement: organizationId={}, entitlement={}",
                    command.organizationId(), ENTITLEMENT_REPORT_HISTORY);
            return Result.failure(new ReportCommandFailure.PlanLimitExceeded(entitlement.get()));
        }

        var readings = monitoringContextFacade.fetchSensorReadingsByOrganizationId(command.organizationId()).stream()
                .filter(reading -> !reading.recordedAt().isBefore(command.periodStart()))
                .filter(reading -> !reading.recordedAt().isAfter(command.periodEnd()))
                .toList();
        var incidents = alertsContextFacade.fetchIncidentsByOrganizationId(command.organizationId()).stream()
                .filter(incident -> incident.detectedAt() != null)
                .filter(incident -> !incident.detectedAt().isBefore(toInstant(command.periodStart())))
                .filter(incident -> !incident.detectedAt().isAfter(toInstant(command.periodEnd())))
                .toList();

        var report = new Report(
                command.organizationId(),
                command.type(),
                command.title(),
                command.periodStart(),
                command.periodEnd(),
                assetCount(command.organizationId(), readings),
                readings.size(),
                outOfRangeCount(readings),
                incidents.size(),
                openIncidentCount(incidents),
                averageTemperature(readings),
                averageHumidity(readings),
                compliancePercentage(readings)
        );
        var saved = reportRepository.save(report);
        log.info("Report generated: id={}, organizationId={}, type={}, readings={}, incidents={}",
                saved.getId(), saved.getOrganizationId(), saved.getType(), saved.getReadingCount(),
                saved.getIncidentCount());
        return Result.success(saved);
    }

    private Instant toInstant(java.time.OffsetDateTime dateTime) {
        return dateTime.toInstant();
    }

    private Integer assetCount(Long organizationId, List<SensorReadingSnapshot> readings) {
        var assetIdsWithReadings = readings.stream()
                .map(SensorReadingSnapshot::assetId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        if (assetIdsWithReadings > 0) {
            return Math.toIntExact(assetIdsWithReadings);
        }
        return assetManagementContextFacade.countAssetsByOrganizationId(organizationId);
    }

    private Integer outOfRangeCount(List<SensorReadingSnapshot> readings) {
        return Math.toIntExact(readings.stream()
                .filter(reading -> Boolean.TRUE.equals(reading.outOfRange()))
                .count());
    }

    private Integer openIncidentCount(List<IncidentSnapshot> incidents) {
        return Math.toIntExact(incidents.stream()
                .filter(IncidentSnapshot::isOpen)
                .count());
    }

    private Double averageTemperature(List<SensorReadingSnapshot> readings) {
        return readings.stream()
                .map(SensorReadingSnapshot::temperature)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .stream()
                .map(value -> Math.round(value * 10.0) / 10.0)
                .boxed()
                .findFirst()
                .orElse(null);
    }

    private Double averageHumidity(List<SensorReadingSnapshot> readings) {
        return readings.stream()
                .map(SensorReadingSnapshot::humidity)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .stream()
                .map(value -> Math.round(value * 10.0) / 10.0)
                .boxed()
                .findFirst()
                .orElse(null);
    }

    private Double compliancePercentage(List<SensorReadingSnapshot> readings) {
        if (readings.isEmpty()) {
            return null;
        }
        var inRange = readings.stream()
                .filter(reading -> !Boolean.TRUE.equals(reading.outOfRange()))
                .count();
        return Math.round((inRange * 1000.0 / readings.size())) / 10.0;
    }
}

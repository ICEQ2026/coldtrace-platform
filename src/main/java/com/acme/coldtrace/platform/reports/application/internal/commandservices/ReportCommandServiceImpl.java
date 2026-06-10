package com.acme.coldtrace.platform.reports.application.internal.commandservices;

import com.acme.coldtrace.platform.alerts.domain.model.aggregates.Incident;
import com.acme.coldtrace.platform.alerts.domain.repositories.IncidentRepository;
import com.acme.coldtrace.platform.assetmanagement.domain.repositories.AssetRepository;
import com.acme.coldtrace.platform.identityaccess.domain.repositories.OrganizationRepository;
import com.acme.coldtrace.platform.monitoring.domain.model.aggregates.SensorReading;
import com.acme.coldtrace.platform.monitoring.domain.repositories.SensorReadingRepository;
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
    private final OrganizationRepository organizationRepository;
    private final AssetRepository assetRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final IncidentRepository incidentRepository;

    public ReportCommandServiceImpl(
            ReportRepository reportRepository,
            OrganizationRepository organizationRepository,
            AssetRepository assetRepository,
            SensorReadingRepository sensorReadingRepository,
            IncidentRepository incidentRepository
    ) {
        this.reportRepository = reportRepository;
        this.organizationRepository = organizationRepository;
        this.assetRepository = assetRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.incidentRepository = incidentRepository;
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
        if (!organizationRepository.existsById(command.organizationId())) {
            log.warn("Organization not found for report generation: organizationId={}", command.organizationId());
            return Result.failure(new ReportCommandFailure.OrganizationNotFound());
        }

        var readings = sensorReadingRepository.findAllByOrganizationId(command.organizationId()).stream()
                .filter(reading -> !reading.getRecordedAt().isBefore(command.periodStart()))
                .filter(reading -> !reading.getRecordedAt().isAfter(command.periodEnd()))
                .toList();
        var incidents = incidentRepository.findAllByOrganizationId(command.organizationId()).stream()
                .filter(incident -> incident.getDetectedAt() != null)
                .filter(incident -> !incident.getDetectedAt().isBefore(toInstant(command.periodStart())))
                .filter(incident -> !incident.getDetectedAt().isAfter(toInstant(command.periodEnd())))
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

    private Integer assetCount(Long organizationId, List<SensorReading> readings) {
        var assetIdsWithReadings = readings.stream()
                .map(SensorReading::getAssetId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        if (assetIdsWithReadings > 0) {
            return Math.toIntExact(assetIdsWithReadings);
        }
        return assetRepository.findAllByOrganizationId(organizationId).size();
    }

    private Integer outOfRangeCount(List<SensorReading> readings) {
        return Math.toIntExact(readings.stream()
                .filter(reading -> Boolean.TRUE.equals(reading.getOutOfRange()))
                .count());
    }

    private Integer openIncidentCount(List<Incident> incidents) {
        return Math.toIntExact(incidents.stream()
                .filter(incident -> !incident.isResolved())
                .count());
    }

    private Double averageTemperature(List<SensorReading> readings) {
        return readings.stream()
                .map(SensorReading::getTemperature)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .stream()
                .map(value -> Math.round(value * 10.0) / 10.0)
                .boxed()
                .findFirst()
                .orElse(null);
    }

    private Double averageHumidity(List<SensorReading> readings) {
        return readings.stream()
                .map(SensorReading::getHumidity)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .stream()
                .map(value -> Math.round(value * 10.0) / 10.0)
                .boxed()
                .findFirst()
                .orElse(null);
    }

    private Double compliancePercentage(List<SensorReading> readings) {
        if (readings.isEmpty()) {
            return null;
        }
        var inRange = readings.stream()
                .filter(reading -> !Boolean.TRUE.equals(reading.getOutOfRange()))
                .count();
        return Math.round((inRange * 1000.0 / readings.size())) / 10.0;
    }
}

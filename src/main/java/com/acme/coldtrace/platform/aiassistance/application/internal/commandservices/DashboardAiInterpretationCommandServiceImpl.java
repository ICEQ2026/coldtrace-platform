package com.acme.coldtrace.platform.aiassistance.application.internal.commandservices;

import com.acme.coldtrace.platform.aiassistance.application.commandservices.AiAssistanceCommandService;
import com.acme.coldtrace.platform.aiassistance.application.commandservices.DashboardAiInterpretationCommandFailure;
import com.acme.coldtrace.platform.aiassistance.application.commandservices.DashboardAiInterpretationCommandService;
import com.acme.coldtrace.platform.aiassistance.application.model.DashboardAiInterpretation;
import com.acme.coldtrace.platform.aiassistance.application.model.DashboardSourceMetric;
import com.acme.coldtrace.platform.aiassistance.domain.model.commands.GenerateDashboardAiInterpretationCommand;
import com.acme.coldtrace.platform.aiassistance.domain.model.commands.GenerateDashboardInterpretationCommand;
import com.acme.coldtrace.platform.alerts.interfaces.acl.AlertsContextFacade;
import com.acme.coldtrace.platform.alerts.interfaces.acl.AlertsContextFacade.IncidentSnapshot;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.AssetSnapshot;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.GatewaySnapshot;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.IoTDeviceSnapshot;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.acl.MaintenanceManagementContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.acl.MaintenanceManagementContextFacade.MaintenanceScheduleSnapshot;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.acl.MaintenanceManagementContextFacade.TechnicalServiceRequestSnapshot;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade.SensorReadingSnapshot;
import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.domain.repositories.ReportRepository;
import com.acme.coldtrace.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Application service that generates dashboard interpretations from backend-owned evidence.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class DashboardAiInterpretationCommandServiceImpl implements DashboardAiInterpretationCommandService {
    private static final int MAX_RECENT_READINGS = 16;
    private static final int MAX_RECENT_INCIDENTS = 12;
    private static final int MAX_ASSET_EVIDENCE = 10;
    private static final int MAX_DEVICE_EVIDENCE = 10;
    private static final int MAX_GATEWAY_EVIDENCE = 10;
    private static final int MAX_RECENT_REPORTS = 5;
    private static final int MAX_MAINTENANCE_RECORDS = 8;

    private final IamContextFacade iamContextFacade;
    private final MonitoringContextFacade monitoringContextFacade;
    private final AlertsContextFacade alertsContextFacade;
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final MaintenanceManagementContextFacade maintenanceManagementContextFacade;
    private final ReportRepository reportRepository;
    private final AiAssistanceCommandService aiAssistanceCommandService;
    private final JsonMapper jsonMapper;

    public DashboardAiInterpretationCommandServiceImpl(
            IamContextFacade iamContextFacade,
            MonitoringContextFacade monitoringContextFacade,
            AlertsContextFacade alertsContextFacade,
            AssetManagementContextFacade assetManagementContextFacade,
            MaintenanceManagementContextFacade maintenanceManagementContextFacade,
            ReportRepository reportRepository,
            AiAssistanceCommandService aiAssistanceCommandService,
            JsonMapper jsonMapper
    ) {
        this.iamContextFacade = iamContextFacade;
        this.monitoringContextFacade = monitoringContextFacade;
        this.alertsContextFacade = alertsContextFacade;
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.maintenanceManagementContextFacade = maintenanceManagementContextFacade;
        this.reportRepository = reportRepository;
        this.aiAssistanceCommandService = aiAssistanceCommandService;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Generates an advisory dashboard interpretation without mutating operational records.
     *
     * @param command command containing organization and optional question
     * @return success with generated interpretation or failure with command/provider error
     */
    @Override
    public Result<DashboardAiInterpretation, DashboardAiInterpretationCommandFailure> handle(
            GenerateDashboardAiInterpretationCommand command) {
        if (!iamContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for dashboard AI interpretation: organizationId={}",
                    command.organizationId());
            return Result.failure(new DashboardAiInterpretationCommandFailure.OrganizationNotFound());
        }

        var context = buildDashboardInterpretationContext(command.organizationId(), command.question());
        var serializedContext = serializeContext(context);
        if (serializedContext.isFailure()) {
            return Result.failure(serializedContext.failure().orElseThrow());
        }

        var generatedInterpretation = aiAssistanceCommandService.handle(
                new GenerateDashboardInterpretationCommand(serializedContext.success().orElseThrow())
        );
        if (generatedInterpretation.isFailure()) {
            return Result.failure(new DashboardAiInterpretationCommandFailure.ProviderFailure(
                    generatedInterpretation.failure().orElseThrow()
            ));
        }

        var response = generatedInterpretation.success().orElseThrow();
        log.info("AI dashboard interpretation generated: organizationId={}, modelProvider={}, modelName={}",
                command.organizationId(), response.modelProvider(), response.modelName());
        return Result.success(new DashboardAiInterpretation(
                command.organizationId(),
                command.question(),
                response.content(),
                context.sourceMetrics(),
                response.modelProvider(),
                response.modelName(),
                OffsetDateTime.now()
        ));
    }

    private Result<String, DashboardAiInterpretationCommandFailure> serializeContext(
            DashboardInterpretationContext context) {
        try {
            return Result.success(jsonMapper.writeValueAsString(context));
        } catch (JacksonException exception) {
            log.warn("Dashboard context serialization failed for AI interpretation", exception);
            return Result.failure(new DashboardAiInterpretationCommandFailure.ContextAssemblyFailed());
        }
    }

    private DashboardInterpretationContext buildDashboardInterpretationContext(Long organizationId, String question) {
        var readings = monitoringContextFacade.fetchSensorReadingsByOrganizationId(organizationId).stream()
                .sorted(this::compareReadingByRecordedAtDesc)
                .toList();
        var incidents = alertsContextFacade.fetchIncidentsByOrganizationId(organizationId).stream()
                .sorted(this::compareIncidentByDetectedAtDesc)
                .toList();
        var reports = reportRepository.findAllByOrganizationId(organizationId).stream()
                .limit(MAX_RECENT_REPORTS)
                .toList();
        var assets = fetchReferencedAssets(organizationId, readings, incidents);
        var devices = fetchReferencedDevices(organizationId, readings);
        var gateways = fetchReferencedGateways(organizationId, readings, devices);
        var maintenanceSchedules = fetchMaintenanceSchedules(organizationId, assets);
        var technicalServiceRequests = fetchTechnicalServiceRequests(organizationId, assets, incidents);
        var metrics = buildMetrics(
                assetManagementContextFacade.countAssetsByOrganizationId(organizationId),
                readings,
                incidents,
                reports,
                maintenanceSchedules,
                technicalServiceRequests,
                devices,
                gateways
        );
        var sourceMetrics = toSourceMetrics(metrics);

        return new DashboardInterpretationContext(
                "dashboard-ai-interpretation.v1",
                organizationId,
                question,
                metrics,
                sourceMetrics,
                readings.stream().limit(MAX_RECENT_READINGS).map(this::toReadingEvidenceContext).toList(),
                incidents.stream().limit(MAX_RECENT_INCIDENTS).map(this::toIncidentEvidenceContext).toList(),
                assets.stream().limit(MAX_ASSET_EVIDENCE).map(this::toAssetEvidenceContext).toList(),
                devices.stream().limit(MAX_DEVICE_EVIDENCE).map(this::toDeviceEvidenceContext).toList(),
                gateways.stream().limit(MAX_GATEWAY_EVIDENCE).map(this::toGatewayEvidenceContext).toList(),
                reports.stream().map(this::toReportEvidenceContext).toList(),
                new MaintenanceEvidenceContext(
                        maintenanceSchedules.stream()
                                .limit(MAX_MAINTENANCE_RECORDS)
                                .map(this::toMaintenanceScheduleEvidenceContext)
                                .toList(),
                        technicalServiceRequests.stream()
                                .limit(MAX_MAINTENANCE_RECORDS)
                                .map(this::toTechnicalServiceRequestEvidenceContext)
                                .toList()
                ),
                buildEvidenceNotes(readings, incidents, assets, devices, gateways, reports,
                        maintenanceSchedules, technicalServiceRequests)
        );
    }

    private List<AssetSnapshot> fetchReferencedAssets(
            Long organizationId,
            List<SensorReadingSnapshot> readings,
            List<IncidentSnapshot> incidents
    ) {
        var assetIds = new LinkedHashMap<Long, Long>();
        readings.stream()
                .map(SensorReadingSnapshot::assetId)
                .filter(Objects::nonNull)
                .forEach(assetId -> assetIds.putIfAbsent(assetId, assetId));
        incidents.stream()
                .map(IncidentSnapshot::assetId)
                .filter(Objects::nonNull)
                .forEach(assetId -> assetIds.putIfAbsent(assetId, assetId));

        return assetIds.keySet().stream()
                .map(assetId -> assetManagementContextFacade.fetchAssetByIdAndOrganizationId(organizationId, assetId))
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparing(AssetSnapshot::id))
                .toList();
    }

    private List<IoTDeviceSnapshot> fetchReferencedDevices(
            Long organizationId,
            List<SensorReadingSnapshot> readings
    ) {
        var deviceIds = new LinkedHashMap<Long, Long>();
        readings.stream()
                .map(SensorReadingSnapshot::iotDeviceId)
                .filter(Objects::nonNull)
                .forEach(deviceId -> deviceIds.putIfAbsent(deviceId, deviceId));

        return deviceIds.keySet().stream()
                .map(deviceId -> assetManagementContextFacade.fetchIoTDeviceByIdAndOrganizationId(
                        organizationId,
                        deviceId
                ))
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparing(IoTDeviceSnapshot::id))
                .toList();
    }

    private List<GatewaySnapshot> fetchReferencedGateways(
            Long organizationId,
            List<SensorReadingSnapshot> readings,
            List<IoTDeviceSnapshot> devices
    ) {
        var gatewayIds = new LinkedHashMap<Long, Long>();
        readings.stream()
                .map(SensorReadingSnapshot::gatewayId)
                .filter(Objects::nonNull)
                .forEach(gatewayId -> gatewayIds.putIfAbsent(gatewayId, gatewayId));
        devices.stream()
                .map(IoTDeviceSnapshot::gatewayId)
                .filter(Objects::nonNull)
                .forEach(gatewayId -> gatewayIds.putIfAbsent(gatewayId, gatewayId));

        return gatewayIds.keySet().stream()
                .map(gatewayId -> assetManagementContextFacade.fetchGatewayByIdAndOrganizationId(
                        organizationId,
                        gatewayId
                ))
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparing(GatewaySnapshot::id))
                .toList();
    }

    private List<MaintenanceScheduleSnapshot> fetchMaintenanceSchedules(
            Long organizationId,
            List<AssetSnapshot> assets
    ) {
        var schedulesById = new LinkedHashMap<Long, MaintenanceScheduleSnapshot>();
        assets.stream()
                .map(AssetSnapshot::id)
                .filter(Objects::nonNull)
                .forEach(assetId -> maintenanceManagementContextFacade
                        .fetchMaintenanceSchedulesByOrganizationIdAndAssetId(organizationId, assetId)
                        .forEach(schedule -> schedulesById.putIfAbsent(schedule.id(), schedule)));
        return schedulesById.values().stream()
                .sorted(this::compareMaintenanceScheduleByScheduledDateAsc)
                .toList();
    }

    private List<TechnicalServiceRequestSnapshot> fetchTechnicalServiceRequests(
            Long organizationId,
            List<AssetSnapshot> assets,
            List<IncidentSnapshot> incidents
    ) {
        var requestsById = new LinkedHashMap<Long, TechnicalServiceRequestSnapshot>();
        incidents.stream()
                .map(IncidentSnapshot::id)
                .filter(Objects::nonNull)
                .forEach(incidentId -> maintenanceManagementContextFacade
                        .fetchTechnicalServiceRequestsByOrganizationIdAndIncidentId(organizationId, incidentId)
                        .forEach(request -> requestsById.putIfAbsent(request.id(), request)));
        assets.stream()
                .map(AssetSnapshot::id)
                .filter(Objects::nonNull)
                .forEach(assetId -> maintenanceManagementContextFacade
                        .fetchTechnicalServiceRequestsByOrganizationIdAndAssetId(organizationId, assetId)
                        .forEach(request -> requestsById.putIfAbsent(request.id(), request)));

        return requestsById.values().stream()
                .sorted(this::compareTechnicalServiceRequestByRequestedAtDesc)
                .toList();
    }

    private DashboardMetricContext buildMetrics(
            int assetCount,
            List<SensorReadingSnapshot> readings,
            List<IncidentSnapshot> incidents,
            List<Report> reports,
            List<MaintenanceScheduleSnapshot> maintenanceSchedules,
            List<TechnicalServiceRequestSnapshot> technicalServiceRequests,
            List<IoTDeviceSnapshot> devices,
            List<GatewaySnapshot> gateways
    ) {
        var readingsCount = readings.size();
        var outOfRangeCount = (int) readings.stream()
                .filter(reading -> Boolean.TRUE.equals(reading.outOfRange()))
                .count();
        var openIncidentCount = (int) incidents.stream()
                .filter(IncidentSnapshot::isOpen)
                .count();
        var averageTemperature = average(readings.stream()
                .map(SensorReadingSnapshot::temperature)
                .filter(Objects::nonNull)
                .toList());
        var averageHumidity = average(readings.stream()
                .map(SensorReadingSnapshot::humidity)
                .filter(Objects::nonNull)
                .toList());
        var thermalCompliance = readingsCount == 0
                ? null
                : roundOneDecimal(((double) (readingsCount - outOfRangeCount) / readingsCount) * 100);

        return new DashboardMetricContext(
                assetCount,
                readingsCount,
                outOfRangeCount,
                thermalCompliance,
                averageTemperature,
                averageHumidity,
                incidents.size(),
                openIncidentCount,
                reports.size(),
                maintenanceSchedules.size(),
                technicalServiceRequests.size(),
                (int) technicalServiceRequests.stream().filter(this::isOpenServiceRequest).count(),
                (int) devices.stream().filter(device -> isUnhealthyStatus(device.status())).count(),
                (int) gateways.stream().filter(gateway -> isUnhealthyStatus(gateway.status())).count()
        );
    }

    private List<DashboardSourceMetric> toSourceMetrics(DashboardMetricContext metrics) {
        var sourceMetrics = new ArrayList<DashboardSourceMetric>();
        sourceMetrics.add(new DashboardSourceMetric(
                "monitoredAssets",
                String.valueOf(metrics.monitoredAssets()),
                "assets",
                "Assets registered for the organization"));
        sourceMetrics.add(new DashboardSourceMetric(
                "readingsReviewed",
                String.valueOf(metrics.readingsReviewed()),
                "readings",
                "Persisted sensor readings reviewed for the dashboard interpretation"));
        sourceMetrics.add(new DashboardSourceMetric(
                "outOfRangeReadings",
                String.valueOf(metrics.outOfRangeReadings()),
                "readings",
                "Readings flagged as outside configured safe ranges"));
        sourceMetrics.add(new DashboardSourceMetric(
                "thermalCompliance",
                metrics.thermalCompliancePercentage() == null
                        ? "unavailable"
                        : metrics.thermalCompliancePercentage().toString(),
                "%",
                "Percentage of reviewed readings inside the configured safe range"));
        sourceMetrics.add(new DashboardSourceMetric(
                "averageTemperature",
                metrics.averageTemperature() == null ? "unavailable" : metrics.averageTemperature().toString(),
                "C",
                "Average temperature across reviewed readings"));
        sourceMetrics.add(new DashboardSourceMetric(
                "averageHumidity",
                metrics.averageHumidity() == null ? "unavailable" : metrics.averageHumidity().toString(),
                "%",
                "Average humidity across reviewed readings"));
        sourceMetrics.add(new DashboardSourceMetric(
                "openIncidents",
                String.valueOf(metrics.openIncidents()),
                "incidents",
                "Incidents that are not resolved"));
        sourceMetrics.add(new DashboardSourceMetric(
                "recentReports",
                String.valueOf(metrics.recentReports()),
                "reports",
                "Most recent persisted reports available to support the dashboard interpretation"));
        sourceMetrics.add(new DashboardSourceMetric(
                "technicalServiceRequests",
                String.valueOf(metrics.technicalServiceRequests()),
                "requests",
                "Technical service requests tied to referenced assets or incidents"));
        sourceMetrics.add(new DashboardSourceMetric(
                "openTechnicalServiceRequests",
                String.valueOf(metrics.openTechnicalServiceRequests()),
                "requests",
                "Technical service requests that are not closed"));
        sourceMetrics.add(new DashboardSourceMetric(
                "maintenanceSchedules",
                String.valueOf(metrics.maintenanceSchedules()),
                "schedules",
                "Preventive maintenance schedules tied to referenced assets"));
        sourceMetrics.add(new DashboardSourceMetric(
                "devicesWithUnhealthyStatus",
                String.valueOf(metrics.devicesWithUnhealthyStatus()),
                "devices",
                "Referenced IoT devices whose status is not online or active"));
        sourceMetrics.add(new DashboardSourceMetric(
                "gatewaysWithUnhealthyStatus",
                String.valueOf(metrics.gatewaysWithUnhealthyStatus()),
                "gateways",
                "Referenced gateways whose status is not online or active"));
        return sourceMetrics;
    }

    private List<String> buildEvidenceNotes(
            List<SensorReadingSnapshot> readings,
            List<IncidentSnapshot> incidents,
            List<AssetSnapshot> assets,
            List<IoTDeviceSnapshot> devices,
            List<GatewaySnapshot> gateways,
            List<Report> reports,
            List<MaintenanceScheduleSnapshot> maintenanceSchedules,
            List<TechnicalServiceRequestSnapshot> technicalServiceRequests
    ) {
        var notes = new ArrayList<String>();
        if (readings.isEmpty()) {
            notes.add("No sensor readings were available for dashboard interpretation.");
        }
        if (incidents.isEmpty()) {
            notes.add("No incidents were available for dashboard interpretation.");
        }
        if (!readings.isEmpty() && assets.isEmpty()) {
            notes.add("Asset details are limited because referenced assets could not be loaded.");
        }
        if (!readings.isEmpty() && devices.isEmpty()) {
            notes.add("IoT device details are limited because referenced devices could not be loaded.");
        }
        if (!readings.isEmpty() && gateways.isEmpty()) {
            notes.add("Gateway details are limited because referenced gateways could not be loaded.");
        }
        if (reports.isEmpty()) {
            notes.add("No persisted reports were available to support trend or compliance interpretation.");
        }
        if (maintenanceSchedules.isEmpty() && technicalServiceRequests.isEmpty()) {
            notes.add("Maintenance progress is limited because no related schedules or technical service requests were found.");
        }
        notes.add("Notification details are not included because the current alerts ACL exposes notifications only through REST.");
        return notes;
    }

    private boolean isOpenServiceRequest(TechnicalServiceRequestSnapshot request) {
        return request.status() == null || !"closed".equalsIgnoreCase(request.status());
    }

    private boolean isUnhealthyStatus(String status) {
        return status != null && !status.equalsIgnoreCase("online") && !status.equalsIgnoreCase("active");
    }

    private Double average(List<Double> values) {
        if (values.isEmpty()) {
            return null;
        }
        return roundOneDecimal(values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
    }

    private Double roundOneDecimal(Double value) {
        return value == null ? null : Math.round(value * 10.0) / 10.0;
    }

    private int compareReadingByRecordedAtDesc(SensorReadingSnapshot left, SensorReadingSnapshot right) {
        if (left.recordedAt() == null && right.recordedAt() == null) {
            return 0;
        }
        if (left.recordedAt() == null) {
            return 1;
        }
        if (right.recordedAt() == null) {
            return -1;
        }
        return right.recordedAt().compareTo(left.recordedAt());
    }

    private int compareIncidentByDetectedAtDesc(IncidentSnapshot left, IncidentSnapshot right) {
        if (left.detectedAt() == null && right.detectedAt() == null) {
            return 0;
        }
        if (left.detectedAt() == null) {
            return 1;
        }
        if (right.detectedAt() == null) {
            return -1;
        }
        return right.detectedAt().compareTo(left.detectedAt());
    }

    private int compareMaintenanceScheduleByScheduledDateAsc(
            MaintenanceScheduleSnapshot left,
            MaintenanceScheduleSnapshot right
    ) {
        if (left.scheduledDate() == null && right.scheduledDate() == null) {
            return 0;
        }
        if (left.scheduledDate() == null) {
            return 1;
        }
        if (right.scheduledDate() == null) {
            return -1;
        }
        return left.scheduledDate().compareTo(right.scheduledDate());
    }

    private int compareTechnicalServiceRequestByRequestedAtDesc(
            TechnicalServiceRequestSnapshot left,
            TechnicalServiceRequestSnapshot right
    ) {
        if (left.requestedAt() == null && right.requestedAt() == null) {
            return 0;
        }
        if (left.requestedAt() == null) {
            return 1;
        }
        if (right.requestedAt() == null) {
            return -1;
        }
        return right.requestedAt().compareTo(left.requestedAt());
    }

    private ReadingEvidenceContext toReadingEvidenceContext(SensorReadingSnapshot reading) {
        return new ReadingEvidenceContext(
                reading.id(),
                reading.assetId(),
                reading.iotDeviceId(),
                reading.gatewayId(),
                reading.locationId(),
                reading.temperature(),
                reading.humidity(),
                reading.outOfRange(),
                reading.recordedAt()
        );
    }

    private IncidentEvidenceContext toIncidentEvidenceContext(IncidentSnapshot incident) {
        return new IncidentEvidenceContext(
                incident.id(),
                incident.assetId(),
                limitText(incident.status(), 80),
                incident.detectedAt(),
                incident.isOpen()
        );
    }

    private AssetEvidenceContext toAssetEvidenceContext(AssetSnapshot asset) {
        return new AssetEvidenceContext(asset.id(), asset.locationId(), limitText(asset.name(), 120));
    }

    private DeviceEvidenceContext toDeviceEvidenceContext(IoTDeviceSnapshot device) {
        return new DeviceEvidenceContext(
                device.id(),
                device.gatewayId(),
                device.assetId(),
                limitText(device.name(), 120),
                limitText(device.status(), 80),
                device.measurementParameters() == null ? List.of() : List.copyOf(device.measurementParameters())
        );
    }

    private GatewayEvidenceContext toGatewayEvidenceContext(GatewaySnapshot gateway) {
        return new GatewayEvidenceContext(
                gateway.id(),
                gateway.locationId(),
                limitText(gateway.status(), 80)
        );
    }

    private ReportEvidenceContext toReportEvidenceContext(Report report) {
        return new ReportEvidenceContext(
                report.getId(),
                limitText(report.getUuid(), 80),
                limitText(report.getType(), 80),
                limitText(report.getTitle(), 160),
                report.getGeneratedAt(),
                report.getReadingCount(),
                report.getOutOfRangeReadingCount(),
                report.getOpenIncidentCount(),
                report.getCompliancePercentage()
        );
    }

    private MaintenanceScheduleEvidenceContext toMaintenanceScheduleEvidenceContext(
            MaintenanceScheduleSnapshot schedule) {
        return new MaintenanceScheduleEvidenceContext(
                schedule.id(),
                schedule.assetId(),
                schedule.scheduledDate(),
                schedule.frequencyDays(),
                schedule.responsibleUserId(),
                limitText(schedule.status(), 80),
                limitText(schedule.observations(), 240)
        );
    }

    private TechnicalServiceRequestEvidenceContext toTechnicalServiceRequestEvidenceContext(
            TechnicalServiceRequestSnapshot request) {
        return new TechnicalServiceRequestEvidenceContext(
                request.id(),
                limitText(request.code(), 80),
                request.assetId(),
                request.incidentId(),
                limitText(request.assetName(), 120),
                limitText(request.issueDescription(), 240),
                limitText(request.priority(), 80),
                limitText(request.status(), 80),
                request.requestedAt(),
                request.closedAt(),
                limitText(request.closureSummary(), 240)
        );
    }

    private String limitText(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        var normalized = value.trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private record DashboardInterpretationContext(
            String contextVersion,
            Long organizationId,
            String operatorQuestion,
            DashboardMetricContext metrics,
            List<DashboardSourceMetric> sourceMetrics,
            List<ReadingEvidenceContext> recentReadings,
            List<IncidentEvidenceContext> recentIncidents,
            List<AssetEvidenceContext> referencedAssets,
            List<DeviceEvidenceContext> referencedDevices,
            List<GatewayEvidenceContext> referencedGateways,
            List<ReportEvidenceContext> recentReports,
            MaintenanceEvidenceContext maintenance,
            List<String> evidenceNotes
    ) {
    }

    private record DashboardMetricContext(
            Integer monitoredAssets,
            Integer readingsReviewed,
            Integer outOfRangeReadings,
            Double thermalCompliancePercentage,
            Double averageTemperature,
            Double averageHumidity,
            Integer incidents,
            Integer openIncidents,
            Integer recentReports,
            Integer maintenanceSchedules,
            Integer technicalServiceRequests,
            Integer openTechnicalServiceRequests,
            Integer devicesWithUnhealthyStatus,
            Integer gatewaysWithUnhealthyStatus
    ) {
    }

    private record ReadingEvidenceContext(
            Long id,
            Long assetId,
            Long iotDeviceId,
            Long gatewayId,
            Long locationId,
            Double temperature,
            Double humidity,
            Boolean outOfRange,
            OffsetDateTime recordedAt
    ) {
    }

    private record IncidentEvidenceContext(
            Long id,
            Long assetId,
            String status,
            Instant detectedAt,
            Boolean open
    ) {
    }

    private record AssetEvidenceContext(Long id, Long locationId, String name) {
    }

    private record DeviceEvidenceContext(
            Long id,
            Long gatewayId,
            Long assetId,
            String name,
            String status,
            List<String> measurementParameters
    ) {
    }

    private record GatewayEvidenceContext(Long id, Long locationId, String status) {
    }

    private record ReportEvidenceContext(
            Long id,
            String uuid,
            String type,
            String title,
            OffsetDateTime generatedAt,
            Integer readingCount,
            Integer outOfRangeReadingCount,
            Integer openIncidentCount,
            Double compliancePercentage
    ) {
    }

    private record MaintenanceEvidenceContext(
            List<MaintenanceScheduleEvidenceContext> schedules,
            List<TechnicalServiceRequestEvidenceContext> technicalServiceRequests
    ) {
    }

    private record MaintenanceScheduleEvidenceContext(
            Long id,
            Long assetId,
            OffsetDateTime scheduledDate,
            Integer frequencyDays,
            Long responsibleUserId,
            String status,
            String observations
    ) {
    }

    private record TechnicalServiceRequestEvidenceContext(
            Long id,
            String code,
            Long assetId,
            Long incidentId,
            String assetName,
            String issueDescription,
            String priority,
            String status,
            OffsetDateTime requestedAt,
            OffsetDateTime closedAt,
            String closureSummary
    ) {
    }
}

package com.acme.coldtrace.platform.reports.application.internal.commandservices;

import com.acme.coldtrace.platform.aiassistance.application.commandservices.AiAssistanceCommandService;
import com.acme.coldtrace.platform.aiassistance.domain.model.commands.GenerateComplianceSummaryCommand;
import com.acme.coldtrace.platform.alerts.interfaces.acl.AlertsContextFacade;
import com.acme.coldtrace.platform.alerts.interfaces.acl.AlertsContextFacade.IncidentSnapshot;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade;
import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.AssetSnapshot;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.acl.MaintenanceManagementContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.acl.MaintenanceManagementContextFacade.TechnicalServiceRequestSnapshot;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import com.acme.coldtrace.platform.monitoring.interfaces.acl.MonitoringContextFacade.SensorReadingSnapshot;
import com.acme.coldtrace.platform.reports.application.commandservices.ReportAiSummaryCommandFailure;
import com.acme.coldtrace.platform.reports.application.commandservices.ReportAiSummaryCommandService;
import com.acme.coldtrace.platform.reports.application.model.ReportAiSummary;
import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.domain.model.commands.GenerateReportAiSummaryCommand;
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
 * Application service that generates advisory report summaries from backend-owned evidence.
 *
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ReportAiSummaryCommandServiceImpl implements ReportAiSummaryCommandService {
    private static final int MAX_READING_EVIDENCE = 12;
    private static final int MAX_INCIDENT_EVIDENCE = 10;
    private static final int MAX_ASSET_EVIDENCE = 10;
    private static final int MAX_CORRECTIVE_ACTION_EVIDENCE = 8;

    private final ReportRepository reportRepository;
    private final IamContextFacade iamContextFacade;
    private final MonitoringContextFacade monitoringContextFacade;
    private final AlertsContextFacade alertsContextFacade;
    private final AssetManagementContextFacade assetManagementContextFacade;
    private final MaintenanceManagementContextFacade maintenanceManagementContextFacade;
    private final AiAssistanceCommandService aiAssistanceCommandService;
    private final JsonMapper jsonMapper;

    public ReportAiSummaryCommandServiceImpl(
            ReportRepository reportRepository,
            IamContextFacade iamContextFacade,
            MonitoringContextFacade monitoringContextFacade,
            AlertsContextFacade alertsContextFacade,
            AssetManagementContextFacade assetManagementContextFacade,
            MaintenanceManagementContextFacade maintenanceManagementContextFacade,
            AiAssistanceCommandService aiAssistanceCommandService,
            JsonMapper jsonMapper
    ) {
        this.reportRepository = reportRepository;
        this.iamContextFacade = iamContextFacade;
        this.monitoringContextFacade = monitoringContextFacade;
        this.alertsContextFacade = alertsContextFacade;
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.maintenanceManagementContextFacade = maintenanceManagementContextFacade;
        this.aiAssistanceCommandService = aiAssistanceCommandService;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Generates an advisory summary without mutating the original report metrics.
     *
     * @param command command containing organization and report identifiers
     * @return success with generated summary or failure with command/provider error
     */
    @Override
    public Result<ReportAiSummary, ReportAiSummaryCommandFailure> handle(GenerateReportAiSummaryCommand command) {
        if (!iamContextFacade.organizationExists(command.organizationId())) {
            log.warn("Organization not found for AI report summary: organizationId={}", command.organizationId());
            return Result.failure(new ReportAiSummaryCommandFailure.OrganizationNotFound());
        }

        var report = reportRepository.findByIdAndOrganizationId(command.reportId(), command.organizationId());
        if (report.isEmpty()) {
            log.warn("Report not found for AI summary: organizationId={}, reportId={}",
                    command.organizationId(), command.reportId());
            return Result.failure(new ReportAiSummaryCommandFailure.ReportNotFound());
        }

        var context = buildReportSummaryContext(command.organizationId(), report.get());
        var serializedContext = serializeContext(context);
        if (serializedContext.isFailure()) {
            return Result.failure(serializedContext.failure().orElseThrow());
        }

        var generatedSummary = aiAssistanceCommandService.handle(
                new GenerateComplianceSummaryCommand(serializedContext.success().orElseThrow())
        );
        if (generatedSummary.isFailure()) {
            return Result.failure(new ReportAiSummaryCommandFailure.ProviderFailure(
                    generatedSummary.failure().orElseThrow()
            ));
        }

        var response = generatedSummary.success().orElseThrow();
        log.info("AI report summary generated: organizationId={}, reportId={}, modelProvider={}, modelName={}",
                command.organizationId(), command.reportId(), response.modelProvider(), response.modelName());
        return Result.success(new ReportAiSummary(
                report.get(),
                response.content(),
                response.modelProvider(),
                response.modelName(),
                OffsetDateTime.now()
        ));
    }

    private Result<String, ReportAiSummaryCommandFailure> serializeContext(ReportSummaryContext context) {
        try {
            return Result.success(jsonMapper.writeValueAsString(context));
        } catch (JacksonException exception) {
            log.warn("Report context serialization failed for AI summary", exception);
            return Result.failure(new ReportAiSummaryCommandFailure.ContextAssemblyFailed());
        }
    }

    private ReportSummaryContext buildReportSummaryContext(Long organizationId, Report report) {
        var readings = monitoringContextFacade.fetchSensorReadingsByOrganizationId(organizationId).stream()
                .filter(reading -> isInReportPeriod(reading.recordedAt(), report))
                .toList();
        var incidents = alertsContextFacade.fetchIncidentsByOrganizationId(organizationId).stream()
                .filter(incident -> incident.detectedAt() != null)
                .filter(incident -> isInReportPeriod(incident.detectedAt(), report))
                .toList();
        var affectedAssets = fetchAffectedAssets(organizationId, readings, incidents);
        var correctiveActions = fetchCorrectiveActions(organizationId, affectedAssets, incidents);

        return new ReportSummaryContext(
                "report-ai-summary.v1",
                toReportContext(report),
                toReportMetricsContext(report),
                readings.stream()
                        .filter(reading -> Boolean.TRUE.equals(reading.outOfRange()))
                        .sorted(this::compareReadingByRecordedAtDesc)
                        .limit(MAX_READING_EVIDENCE)
                        .map(this::toReadingEvidenceContext)
                        .toList(),
                incidents.stream()
                        .sorted(this::compareIncidentByDetectedAtDesc)
                        .limit(MAX_INCIDENT_EVIDENCE)
                        .map(this::toIncidentEvidenceContext)
                        .toList(),
                affectedAssets.stream()
                        .limit(MAX_ASSET_EVIDENCE)
                        .map(this::toAssetEvidenceContext)
                        .toList(),
                correctiveActions.stream()
                        .limit(MAX_CORRECTIVE_ACTION_EVIDENCE)
                        .map(this::toCorrectiveActionEvidenceContext)
                        .toList(),
                buildEvidenceNotes(report, readings, incidents, correctiveActions)
        );
    }

    private boolean isInReportPeriod(OffsetDateTime recordedAt, Report report) {
        if (recordedAt == null) {
            return false;
        }
        return !recordedAt.isBefore(report.getPeriodStart()) && !recordedAt.isAfter(report.getPeriodEnd());
    }

    private boolean isInReportPeriod(Instant detectedAt, Report report) {
        if (detectedAt == null) {
            return false;
        }
        var start = report.getPeriodStart().toInstant();
        var end = report.getPeriodEnd().toInstant();
        return !detectedAt.isBefore(start) && !detectedAt.isAfter(end);
    }

    private List<AssetSnapshot> fetchAffectedAssets(
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
                .map(assetId -> assetManagementContextFacade.fetchAssetByIdAndOrganizationId(
                        organizationId,
                        assetId
                ))
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparing(AssetSnapshot::id))
                .toList();
    }

    private List<TechnicalServiceRequestSnapshot> fetchCorrectiveActions(
            Long organizationId,
            List<AssetSnapshot> affectedAssets,
            List<IncidentSnapshot> incidents
    ) {
        var correctiveActions = new LinkedHashMap<Long, TechnicalServiceRequestSnapshot>();
        incidents.stream()
                .map(IncidentSnapshot::id)
                .filter(Objects::nonNull)
                .forEach(incidentId -> maintenanceManagementContextFacade
                        .fetchTechnicalServiceRequestsByOrganizationIdAndIncidentId(organizationId, incidentId)
                        .forEach(request -> correctiveActions.putIfAbsent(request.id(), request)));
        affectedAssets.stream()
                .map(AssetSnapshot::id)
                .filter(Objects::nonNull)
                .forEach(assetId -> maintenanceManagementContextFacade
                        .fetchTechnicalServiceRequestsByOrganizationIdAndAssetId(organizationId, assetId)
                        .forEach(request -> correctiveActions.putIfAbsent(request.id(), request)));

        return correctiveActions.values().stream()
                .sorted(this::compareTechnicalServiceRequestByRequestedAtDesc)
                .toList();
    }

    private List<String> buildEvidenceNotes(
            Report report,
            List<SensorReadingSnapshot> readings,
            List<IncidentSnapshot> incidents,
            List<TechnicalServiceRequestSnapshot> correctiveActions
    ) {
        var notes = new ArrayList<String>();
        if (report.getReadingCount() == null || report.getReadingCount() == 0 || readings.isEmpty()) {
            notes.add("No sensor readings were available in the report period.");
        }
        if (report.getOutOfRangeReadingCount() != null && report.getOutOfRangeReadingCount() > 0 &&
                readings.stream().noneMatch(reading -> Boolean.TRUE.equals(reading.outOfRange()))) {
            notes.add("The report stores out-of-range metrics, but detailed out-of-range readings are unavailable.");
        }
        if (report.getIncidentCount() != null && report.getIncidentCount() > 0 && incidents.isEmpty()) {
            notes.add("The report stores incident metrics, but detailed incident records are unavailable.");
        }
        if (!incidents.isEmpty() && correctiveActions.isEmpty()) {
            notes.add("No corrective action evidence was found for incidents or affected assets in this report.");
        }
        if (report.getCompliancePercentage() == null) {
            notes.add("Compliance percentage is unavailable because the report has no eligible readings.");
        }
        return notes;
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

    private ReportContext toReportContext(Report report) {
        return new ReportContext(
                report.getId(),
                report.getOrganizationId(),
                limitText(report.getUuid(), 80),
                limitText(report.getType(), 80),
                limitText(report.getTitle(), 160),
                report.getPeriodStart(),
                report.getPeriodEnd(),
                report.getGeneratedAt()
        );
    }

    private ReportMetricsContext toReportMetricsContext(Report report) {
        return new ReportMetricsContext(
                report.getAssetCount(),
                report.getReadingCount(),
                report.getOutOfRangeReadingCount(),
                report.getIncidentCount(),
                report.getOpenIncidentCount(),
                report.getAverageTemperature(),
                report.getAverageHumidity(),
                report.getCompliancePercentage()
        );
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

    private CorrectiveActionEvidenceContext toCorrectiveActionEvidenceContext(
            TechnicalServiceRequestSnapshot request
    ) {
        return new CorrectiveActionEvidenceContext(
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
                limitText(request.closureSummary(), 240),
                limitText(request.evidence(), 240),
                limitText(request.closedBy(), 120)
        );
    }

    private String limitText(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private record ReportSummaryContext(
            String contextVersion,
            ReportContext report,
            ReportMetricsContext metrics,
            List<ReadingEvidenceContext> outOfRangeReadings,
            List<IncidentEvidenceContext> incidents,
            List<AssetEvidenceContext> affectedAssets,
            List<CorrectiveActionEvidenceContext> correctiveActions,
            List<String> evidenceNotes
    ) {
    }

    private record ReportContext(
            Long id,
            Long organizationId,
            String uuid,
            String type,
            String title,
            OffsetDateTime periodStart,
            OffsetDateTime periodEnd,
            OffsetDateTime generatedAt
    ) {
    }

    private record ReportMetricsContext(
            Integer assetCount,
            Integer readingCount,
            Integer outOfRangeReadingCount,
            Integer incidentCount,
            Integer openIncidentCount,
            Double averageTemperature,
            Double averageHumidity,
            Double compliancePercentage
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

    private record CorrectiveActionEvidenceContext(
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
            String closureSummary,
            String evidence,
            String closedBy
    ) {
    }
}

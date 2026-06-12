package com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates;

import com.acme.coldtrace.platform.assetmanagement.interfaces.acl.AssetManagementContextFacade.AssetSnapshot;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateTechnicalServiceRequestCommand;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.UpdateTechnicalServiceRequestStatusCommand;
import com.acme.coldtrace.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class TechnicalServiceRequest extends AbstractDomainAggregateRoot<TechnicalServiceRequest> {
    public static final Set<String> ALLOWED_STATUSES = Set.of("open", "in_progress", "closed", "canceled");
    private static final Set<String> TERMINAL_STATUSES = Set.of("closed", "canceled");

    private Long id;
    private Long organizationId;
    private String code;
    private Long assetId;
    private Long assetLocationId;
    private String assetName;
    private Long incidentId;
    private String issueDescription;
    private String priority;
    private String status;
    private String requestedBy;
    private OffsetDateTime requestedAt;
    private OffsetDateTime closedAt;
    private String closureSummary;
    private String evidence;
    private String closedBy;

    protected TechnicalServiceRequest() {
    }

    public TechnicalServiceRequest(CreateTechnicalServiceRequestCommand command, AssetSnapshot asset) {
        this.organizationId = command.organizationId();
        this.code = "TSR-" + UUID.randomUUID();
        this.assetId = command.assetId();
        this.assetLocationId = asset.locationId();
        this.assetName = asset.name();
        this.incidentId = command.incidentId();
        this.issueDescription = command.issueDescription();
        this.priority = command.priority();
        this.status = "open";
        this.requestedBy = command.requestedBy();
        this.requestedAt = OffsetDateTime.now();
    }

    public TechnicalServiceRequest(Long id, Long organizationId, String code, Long assetId, Long assetLocationId,
            String assetName, Long incidentId, String issueDescription, String priority, String status,
            String requestedBy, OffsetDateTime requestedAt, OffsetDateTime closedAt, String closureSummary,
            String evidence, String closedBy) {
        this.id = id;
        this.organizationId = organizationId;
        this.code = code;
        this.assetId = assetId;
        this.assetLocationId = assetLocationId;
        this.assetName = assetName;
        this.incidentId = incidentId;
        this.issueDescription = issueDescription;
        this.priority = priority;
        this.status = status;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
        this.closedAt = closedAt;
        this.closureSummary = closureSummary;
        this.evidence = evidence;
        this.closedBy = closedBy;
    }

    public boolean canTransitionTo(String requestedStatus) {
        if (status.equals(requestedStatus)) return true;
        if (TERMINAL_STATUSES.contains(status)) return false;
        if ("open".equals(status)) return Set.of("in_progress", "closed", "canceled").contains(requestedStatus);
        if ("in_progress".equals(status)) return Set.of("closed", "canceled").contains(requestedStatus);
        return false;
    }

    public void updateStatus(UpdateTechnicalServiceRequestStatusCommand command) {
        this.status = command.status();
        if ("closed".equals(command.status())) {
            this.closedAt = OffsetDateTime.now();
            this.closureSummary = command.closureSummary();
            this.evidence = command.evidence();
            this.closedBy = command.closedBy();
        }
    }
}

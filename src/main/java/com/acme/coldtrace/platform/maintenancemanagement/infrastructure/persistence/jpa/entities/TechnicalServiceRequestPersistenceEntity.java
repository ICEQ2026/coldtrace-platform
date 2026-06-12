package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "technical_service_requests")
public class TechnicalServiceRequestPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false) private Long organizationId;
    @Column(nullable = false) private String code;
    @Column(nullable = false) private Long assetId;
    @Column(nullable = false) private Long assetLocationId;
    @Column(nullable = false) private String assetName;
    @Column private Long incidentId;
    @Column(nullable = false, length = 1000) private String issueDescription;
    @Column(nullable = false) private String priority;
    @Column(nullable = false) private String status;
    @Column private String requestedBy;
    @Column(nullable = false) private OffsetDateTime requestedAt;
    @Column private OffsetDateTime closedAt;
    @Column(length = 1000) private String closureSummary;
    @Column(length = 1000) private String evidence;
    @Column private String closedBy;
}

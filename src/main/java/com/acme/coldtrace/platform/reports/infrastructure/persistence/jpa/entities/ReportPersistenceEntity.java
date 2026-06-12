package com.acme.coldtrace.platform.reports.infrastructure.persistence.jpa.entities;

import com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * JPA persistence entity for generated reports.
 *
 * @since 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "reports")
public class ReportPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private OffsetDateTime periodStart;

    @Column(nullable = false)
    private OffsetDateTime periodEnd;

    @Column(nullable = false)
    private OffsetDateTime generatedAt;

    @Column(nullable = false)
    private Integer assetCount;

    @Column(nullable = false)
    private Integer readingCount;

    @Column(nullable = false)
    private Integer outOfRangeReadingCount;

    @Column(nullable = false)
    private Integer incidentCount;

    @Column(nullable = false)
    private Integer openIncidentCount;

    @Column
    private Double averageTemperature;

    @Column
    private Double averageHumidity;

    @Column
    private Double compliancePercentage;
}

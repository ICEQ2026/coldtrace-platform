package com.acme.coldtrace.platform.reports.interfaces.rest.transform;

import com.acme.coldtrace.platform.reports.domain.model.aggregates.Report;
import com.acme.coldtrace.platform.reports.interfaces.rest.resources.ReportResource;

import java.time.format.DateTimeFormatter;

/**
 * Assembler that converts report aggregates into REST resources.
 *
 * @since 1.0
 */
public class ReportResourceFromEntityAssembler {
    /**
     * Converts a report aggregate into a resource.
     *
     * @param entity domain aggregate
     * @return REST resource with report data and metrics
     */
    public static ReportResource toResourceFromEntity(Report entity) {
        return new ReportResource(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getUuid(),
                entity.getType(),
                entity.getTitle(),
                periodLabel(entity),
                entity.getPeriodStart(),
                entity.getPeriodEnd(),
                entity.getGeneratedAt(),
                entity.getAssetCount(),
                entity.getReadingCount(),
                entity.getOutOfRangeReadingCount(),
                entity.getIncidentCount(),
                entity.getOpenIncidentCount(),
                entity.getAverageTemperature(),
                entity.getAverageHumidity(),
                entity.getCompliancePercentage()
        );
    }

    private static String periodLabel(Report entity) {
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return entity.getPeriodStart().format(formatter) + "/" + entity.getPeriodEnd().format(formatter);
    }
}

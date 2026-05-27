package org.example.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.example.application.dto.report.GeneratedReport;
import org.example.application.dto.report.Report;
import org.example.application.dto.report.ReportType;
import org.example.application.export.ReportDocumentBuilder;
import org.example.application.report.ReportTypeNameResolver;
import org.example.application.service.report.ReportBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private final ReportTypeNameResolver reportTypeNameResolver;
    private final ReportDocumentBuilder reportDocumentBuilder;
    private final Map<String, ReportBuilder> reportBuildersByType;
    private final Map<UUID, Report> reportsById;

    public ReportService(List<ReportBuilder> reportBuilders,
                         ReportTypeNameResolver reportTypeNameResolver,
                         ReportDocumentBuilder reportDocumentBuilder) {
        this.reportTypeNameResolver = reportTypeNameResolver;
        this.reportDocumentBuilder = reportDocumentBuilder;
        this.reportBuildersByType = reportBuilders.stream()
                .collect(Collectors.toMap(ReportBuilder::getBuildedReportType, Function.identity()));
        this.reportsById = new ConcurrentHashMap<>();
    }

    public List<ReportType> getAll() {
        if (CollectionUtils.isEmpty(reportBuildersByType)) {
            return List.of();
        }

        return reportBuildersByType.keySet().stream()
                .map(key -> new ReportType(key, reportTypeNameResolver.resolve(key)))
                .toList();
    }

    public GeneratedReport generateReport(String reportType, Map<String, String> params, String username) {
        ReportBuilder reportBuilder = reportBuildersByType.get(reportType);

        if (reportBuilder == null) {
            throw new RuntimeException();
        }

        Map<String, Object> reportParams = new HashMap<>(params);
        reportParams.put("username", username);

        Report report = reportBuilder.buildReport(reportParams);
        UUID reportId = UUID.randomUUID();
        reportsById.put(reportId, report);

        return new GeneratedReport(reportId, report);
    }

    public byte[] buildPdfReport(UUID id) {
        Report report = reportsById.get(id);

        if (report == null) {
            throw new RuntimeException();
        }

        return reportDocumentBuilder.buildDocumentFromReport(report);
    }
}

package org.example.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.example.config.ReportProp;
import org.example.dto.report.GeneratedReport;
import org.example.dto.report.Report;
import org.example.dto.report.ReportType;
import org.example.service.report.ReportBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private final ReportProp reportProp;
    private final ReportPdfService reportPdfService;
    private final Map<String, ReportBuilder> reportBuildersByType;
    private final Map<UUID, Report> reportsById;

    public ReportService(List<ReportBuilder> reportBuilders, ReportProp reportProp, ReportPdfService reportPdfService) {
        this.reportProp = reportProp;
        this.reportPdfService = reportPdfService;
        this.reportBuildersByType = reportBuilders.stream()
                .collect(Collectors.toMap(ReportBuilder::getBuildedReportType, Function.identity()));
        this.reportsById = new ConcurrentHashMap<>();
    }

    public List<ReportType> getAll() {
        if (CollectionUtils.isEmpty(reportBuildersByType)) {
            return List.of();
        }

        return reportBuildersByType.keySet().stream()
                .map(key -> new ReportType(key, reportProp.getTranslations().getOrDefault(key, key)))
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

        return reportPdfService.buildDocumentFromReport(report);
    }
}

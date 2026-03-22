package org.example.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.example.config.ReportProp;
import org.example.service.report.ReportBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private final ReportProp reportProp;
    private final Map<String, ReportBuilder> reportBuildersByType;

    public ReportService(List<ReportBuilder> reportBuilders, ReportProp reportProp) {
        this.reportProp = reportProp;
        this.reportBuildersByType = reportBuilders.stream()
                .collect(Collectors.toMap(ReportBuilder::getBuildedReportType, Function.identity()));
    }

    public List<String> getAll() {
        if (CollectionUtils.isEmpty(reportBuildersByType)) {
            return List.of();
        }

        return reportBuildersByType.keySet().stream()
                .map(n -> reportProp.getTranslations().get(n))
                .toList();
    }
}

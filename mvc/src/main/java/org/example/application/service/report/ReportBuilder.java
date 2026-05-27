package org.example.application.service.report;

import org.example.application.dto.report.Report;
import java.util.Map;

public interface ReportBuilder {
    String getBuildedReportType();

    Report buildReport(Map<String, Object> params);
}

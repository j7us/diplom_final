package org.example.service.report;

import org.example.dto.report.Report;
import java.util.Map;

public interface ReportBuilder {
    String getBuildedReportType();

    Report buildReport(Map<String, Object> params);
}

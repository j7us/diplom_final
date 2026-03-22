package org.example.service.report;

import java.util.Map;

public interface ReportBuilder {
    String getBuildedReportType();

    Object buildReport(Map<String, Object> params);
}

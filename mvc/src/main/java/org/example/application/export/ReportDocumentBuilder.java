package org.example.application.export;

import org.example.application.dto.report.Report;

public interface ReportDocumentBuilder {
    byte[] buildDocumentFromReport(Report report);
}

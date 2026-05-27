package org.example.adapter.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.application.dto.report.GeneratedReport;
import org.example.application.dto.report.ReportType;
import org.example.application.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Отчеты", description = "Эндпоинты создания отчетов")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/reports")
    public List<ReportType> getReports() {
        log.info("Пришел запрос /api/reports");

        return reportService.getAll();
    }

    @PostMapping("/reports/{reportType}")
    public ResponseEntity<GeneratedReport> getReport(@PathVariable String reportType,
                                                     @RequestBody Map<String, String> params,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/reports/{reportType} с параметрами: {}, {}, {}", reportType, params, userDetails.getUsername());

        GeneratedReport report = reportService.generateReport(reportType, params, userDetails.getUsername());

        return ResponseEntity.ok(report);
    }

    @GetMapping(value = "/reports/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReportPdf(@PathVariable UUID id) {
        log.info("Пришел запрос /api/reports/{id}/pdf с параметрами: {}", id);

        byte[] file= reportService.buildPdfReport(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}

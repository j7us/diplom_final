package org.example.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.dto.report.Report;
import org.example.dto.report.ReportType;
import org.example.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/reports")
    public List<ReportType> getReports() {
        return reportService.getAll();
    }

    @PostMapping("/reports/{reportType}")
    public ResponseEntity<Report> getReport(@PathVariable String reportType,
                                            @RequestBody Map<String, String> params,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Report report = reportService.getReport(reportType, params, userDetails.getUsername());

        return ResponseEntity.ok(report);
    }
}

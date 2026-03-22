package org.example.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.report.ReportType;
import org.example.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
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
}

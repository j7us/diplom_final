package org.example.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import org.example.dto.report.Report;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ReportPdfService {

    public byte[] buildDocumentFromReport(Report report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont("/System/Library/Fonts/Supplemental/Arial.ttf", PdfEncodings.IDENTITY_H);
            Table table = new Table(2);
            table.setMarginTop(5);

            fillTableColumnNames(table, report, font);

            Map<String, BigDecimal> result = report.getResult() == null ? Map.of() : report.getResult();
            result.forEach((key, value) -> {
                table.addCell(createCell(key, font));
                table.addCell(createCell(value.toPlainString(), font));
            });

            document.add(new Paragraph(report.getName()).setFont(font));
            document.add(table);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillTableColumnNames(Table table, Report report, PdfFont font) {
        table.addHeaderCell(createCell("Интервал", font));
        table.addHeaderCell(createCell(resolveColumnName(report), font));
    }

    private Cell createCell(String value, PdfFont font) {
        return new Cell().add(new Paragraph(value).setFont(font));
    }

    private String resolveColumnName(Report report) {
        String reportName = report.getName();

        return reportName.contains("Пробег") ? "Дистанция" : "Количество";
    }
}

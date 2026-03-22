package org.example.service.report;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.report.Report;
import org.example.entity.Vehicle;
import org.example.service.VehicleService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleProductionReportBuilder implements ReportBuilder {
    private static final DateTimeFormatter REPORT_NAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VehicleService vehicleService;

    @Override
    public String getBuildedReportType() {
        return "vehicleProductionReport";
    }

    @Override
    public Report buildReport(Map<String, Object> params) {
        UUID enterpriseId = UUID.fromString((String) params.get("enterpriseId"));
        Instant dateFrom = Instant.parse((String)params.get("dateFrom"));
        Instant dateTo = Instant.parse((String)params.get("dateTo"));
        String username = (String) params.get("username");
        String intervalType = (String) params.get("intervalType");

        List<Vehicle> vehicles = vehicleService.getEntitiesByEnterpriseAndManagerUsernameAndProductionDateBetween(
                enterpriseId,
                dateFrom,
                dateTo,
                username);

        Map<String, BigDecimal> result = aggregateVehicles(vehicles, intervalType);
        String name = "Новых автомобилей за период "
                + formatInstantForName(dateFrom)
                + " - "
                + formatInstantForName(dateTo);

        return new Report(name, intervalType, dateFrom, dateTo, result);
    }

    private Map<String, BigDecimal> aggregateVehicles(List<Vehicle> vehicles, String intervalType) {
        Map<String, BigDecimal> result = new TreeMap<>();

        vehicles.forEach(vehicle -> result.merge(
                getIntervalKey(vehicle.getProductionDate(), intervalType),
                BigDecimal.ONE,
                BigDecimal::add));

        return result;
    }

    private String getIntervalKey(Instant date, String intervalType) {
        LocalDate localDate = date.atZone(ZoneOffset.UTC).toLocalDate();

        return switch (intervalType) {
            case "day" -> localDate.toString();
            case "month" -> YearMonth.from(localDate).toString();
            case "year" -> String.valueOf(localDate.getYear());
            default -> throw new RuntimeException();
        };
    }

    private String formatInstantForName(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).toLocalDateTime().format(REPORT_NAME_DATE_FORMATTER);
    }
}

package org.example.service.report;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.example.dto.report.Report;
import org.example.entity.Trip;
import org.example.service.TripService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VehicleMileageReportBuilder implements ReportBuilder {
    private static final DateTimeFormatter REPORT_NAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TripService tripService;
    private final Map<String, Function<Trip, List<String>>> intervalTripExecutors;

    public VehicleMileageReportBuilder(TripService tripService) {
        this.tripService = tripService;
        intervalTripExecutors = Map.of(
                "day", this::buildDayKeys,
                "month", this::buildMonthKeys,
                "year", this::buildYearKeys
        );
    }

    @Override
    public String getBuildedReportType() {
        return "vehicleMileageReport";
    }

    @Override
    public Report buildReport(Map<String, Object> params) {
        UUID vehicleId = UUID.fromString((String) params.get("vehicleId"));
        Instant dateFrom = Instant.parse((String)params.get("dateFrom"));
        Instant dateTo = Instant.parse((String)params.get("dateTo"));
        String username = (String) params.get("username");
        String intervalType = (String) params.get("intervalType");

        List<Trip> trips = tripService.getTripEntities(vehicleId, dateFrom, dateTo, username);
        Map<String, BigDecimal> result = aggregateTrips(trips, intervalType);
        String name = "Пробег автомобиля за период "
                + formatInstantForName(dateFrom)
                + " - "
                + formatInstantForName(dateTo);

        return new Report(name, intervalType, dateFrom, dateTo, result);
    }

    private Map<String, BigDecimal> aggregateTrips(List<Trip> trips, String intervalType) {
        Map<String, BigDecimal> result = new HashMap<>();

        trips.forEach(trip -> intervalTripExecutors.get(intervalType).apply(trip)
                .forEach(key -> result.merge(key, trip.getDistance(), BigDecimal::add)));

        return result;
    }

    private List<String> buildDayKeys(Trip trip) {
        LocalDate start = trip.getDateFrom().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate end = trip.getDateTo().atZone(ZoneOffset.UTC).toLocalDate();
        List<String> keys = new ArrayList<>();

        while (!start.isAfter(end)) {
            keys.add(start.toString());
            start = start.plusDays(1);
        }

        return keys;
    }

    private List<String> buildMonthKeys(Trip trip) {
        YearMonth start = YearMonth.from(trip.getDateFrom().atZone(ZoneOffset.UTC));
        YearMonth end = YearMonth.from(trip.getDateTo().atZone(ZoneOffset.UTC));
        List<String> keys = new ArrayList<>();

        while (start.compareTo(end) <= 0) {
            keys.add(start.toString());
            start = start.plusMonths(1);
        }

        return keys;
    }

    private List<String> buildYearKeys(Trip trip) {
        int start = trip.getDateFrom().atZone(ZoneOffset.UTC).getYear();
        int end = trip.getDateTo().atZone(ZoneOffset.UTC).getYear();
        List<String> keys = new ArrayList<>();

        while (start <= end) {
            keys.add(String.valueOf(start));
            start++;
        }

        return keys;
    }

    private String formatInstantForName(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).toLocalDateTime().format(REPORT_NAME_DATE_FORMATTER);
    }
}

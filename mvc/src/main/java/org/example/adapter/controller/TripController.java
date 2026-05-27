package org.example.adapter.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.controller.dto.trip.TripRestDto;
import org.example.adapter.controller.dto.vehiclelocation.VehicleLocationGeoJsonRestDto;
import org.example.adapter.controller.mapper.TripMapper;
import org.example.adapter.controller.mapper.VehicleLocationMapper;
import org.example.adapter.file.GpxParserService;
import org.example.application.service.TripService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Поездки", description = "Эндпоинты создания и загрузки поездок")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TripController {
    private final TripService tripService;
    private final GpxParserService gpxParserService;
    private final TripMapper tripMapper;
    private final VehicleLocationMapper vehicleLocationMapper;

    @Operation(summary = "Создание поездки с локациями из gpx файла")
    @PostMapping(value = "vehicles/{vehicleId}/trips/gpx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importGpx(@PathVariable UUID vehicleId,
                                          @RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicles/{vehicleId}/trips/gpx с параметрами: {}, {}, {}", vehicleId, file.getOriginalFilename(), userDetails.getUsername());

        gpxParserService.parseLocations(vehicleId, file, userDetails.getUsername());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение локаций для поездки в геометрии")
    @GetMapping("vehicles/{vehicleId}/trips")
    public List<VehicleLocationGeoJsonRestDto> getVehicleLocations(@PathVariable UUID vehicleId,
                                                                   @RequestParam Instant dateFrom,
                                                                   @RequestParam Instant dateTo,
                                                                   @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicles/{vehicleId}/trips с параметрами: {}, {}, {}", vehicleId, dateFrom, dateTo);

        return tripService.getVehicleLocations(vehicleId, dateFrom, dateTo, userDetails.getUsername())
                .stream()
                .map(vehicleLocationMapper::toGeoJsonDto)
                .toList();
    }

    @Operation(summary = "Получение локаций для поездки в json")
    @GetMapping("vehicles/{vehicleId}/trip-details")
    public List<TripRestDto> getTrips(@PathVariable UUID vehicleId,
                                      @RequestParam Instant dateFrom,
                                      @RequestParam Instant dateTo,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicles/{vehicleId}/trip-details с параметрами: {}, {}, {}", vehicleId, dateFrom, dateTo);

        return tripService.getTrips(vehicleId, dateFrom, dateTo, userDetails.getUsername())
                .stream()
                .map(tripMapper::toDto)
                .toList();
    }
}

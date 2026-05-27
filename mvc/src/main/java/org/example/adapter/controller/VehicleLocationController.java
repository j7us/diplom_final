package org.example.adapter.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.controller.dto.vehiclelocation.VehicleLocationCreateRestDto;
import org.example.adapter.controller.dto.vehiclelocation.VehicleLocationJsonRestDto;
import org.example.adapter.controller.mapper.VehicleLocationMapper;
import org.example.application.service.VehicleLocationService;
import org.example.model.VehicleLocation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Локации автомобилей", description = "Эндпоинты для создания и получения локаций автомобиля")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class VehicleLocationController {
    private final VehicleLocationService vehicleLocationService;
    private final VehicleLocationMapper vehicleLocationMapper;

    @PostMapping("/vehicle/{vehicleId}/locations")
    public VehicleLocationJsonRestDto createLocation(@PathVariable UUID vehicleId,
                                                     @RequestBody VehicleLocationCreateRestDto dto,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicle/{vehicleId}/locations с параметрами: {}, {}, {}", vehicleId, dto.getLatitude(), dto.getLongitude());

        VehicleLocation location = vehicleLocationMapper.toModel(dto);
        VehicleLocation savedLocation = vehicleLocationService.create(vehicleId, location, userDetails.getUsername());

        return vehicleLocationMapper.toJsonDto(savedLocation);
    }

    @GetMapping("/vehicle/{vehicleId}/locations")
    public List<?> getLocations(@PathVariable UUID vehicleId,
                                @RequestParam LocalDateTime dateFrom,
                                @RequestParam LocalDateTime dateTo,
                                @RequestParam(defaultValue = "json") String format,
                                @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicle/{vehicleId}/locations с параметрами: {}, {}, {}", vehicleId, dateFrom + " - " + dateTo, format);

        if ("geojson".equalsIgnoreCase(format)) {
            return vehicleLocationService.getAll(vehicleId, dateFrom, dateTo, userDetails.getUsername())
                    .stream()
                    .map(vehicleLocationMapper::toGeoJsonDto)
                    .toList();
        }

        return vehicleLocationService.getAll(vehicleId, dateFrom, dateTo, userDetails.getUsername())
                .stream()
                .map(vehicleLocationMapper::toJsonDto)
                .toList();
    }
}

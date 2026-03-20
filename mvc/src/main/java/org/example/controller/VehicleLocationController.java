package org.example.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.vehiclelocation.VehicleLocationCreateRestDto;
import org.example.dto.vehiclelocation.VehicleLocationJsonRestDto;
import org.example.service.VehicleLocationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehicleLocationController {
    private final VehicleLocationService vehicleLocationService;

    @PostMapping("/vehicle/{vehicleId}/locations")
    public VehicleLocationJsonRestDto createLocation(@PathVariable UUID vehicleId,
                                                     @RequestBody VehicleLocationCreateRestDto dto,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        return vehicleLocationService.create(vehicleId, dto, userDetails.getUsername());
    }

    @GetMapping("/vehicle/{vehicleId}/locations")
    public List<?> getLocations(@PathVariable UUID vehicleId,
                                @RequestParam LocalDateTime dateFrom,
                                @RequestParam LocalDateTime dateTo,
                                @RequestParam(defaultValue = "json") String format,
                                @AuthenticationPrincipal UserDetails userDetails) {
        if ("geojson".equalsIgnoreCase(format)) {
            return vehicleLocationService.getAllGeoJson(vehicleId, dateFrom, dateTo, userDetails.getUsername());
        }

        return vehicleLocationService.getAllJson(vehicleId, dateFrom, dateTo, userDetails.getUsername());
    }
}

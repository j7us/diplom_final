package org.example.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.trip.TripRestDto;
import org.example.dto.vehiclelocation.VehicleLocationGeoJsonRestDto;
import org.example.service.TripService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @GetMapping("vehicles/{vehicleId}/trips")
    public List<VehicleLocationGeoJsonRestDto> getVehicleLocations(@PathVariable UUID vehicleId,
                                                                    @RequestParam Instant dateFrom,
                                                                    @RequestParam Instant dateTo,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        return tripService.getVehicleLocations(vehicleId, dateFrom, dateTo, userDetails.getUsername());
    }

    @GetMapping("vehicles/{vehicleId}/trip-details")
    public List<TripRestDto> getTrips(@PathVariable UUID vehicleId,
                                      @RequestParam Instant dateFrom,
                                      @RequestParam Instant dateTo,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        return tripService.getTrips(vehicleId, dateFrom, dateTo, userDetails.getUsername());
    }
}

package org.example.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.trip.TripRestDto;
import org.example.dto.vehiclelocation.VehicleLocationGeoJsonRestDto;
import org.example.service.GpxParserService;
import org.example.service.TripService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
    private final GpxParserService gpxParserService;

    @PostMapping(value = "vehicles/{vehicleId}/trips/gpx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importGpx(@PathVariable UUID vehicleId,
                                          @RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        gpxParserService.parseLocations(vehicleId, file, userDetails.getUsername());

        return ResponseEntity.ok().build();
    }

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

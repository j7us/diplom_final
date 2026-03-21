package org.example.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.vehiclelocation.VehicleLocationGeoJsonRestDto;
import org.example.entity.Trip;
import org.example.entity.Vehicle;
import org.example.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {
    private final TripRepository tripRepository;
    private final VehicleService vehicleService;
    private final VehicleLocationService vehicleLocationService;

    public List<VehicleLocationGeoJsonRestDto> getVehicleLocations(UUID vehicleId,
                                                                   Instant dateFrom,
                                                                   Instant dateTo,
                                                                   String username) {
        Vehicle vehicle = vehicleService.getEntityByIdAndManagerUsername(vehicleId, username);

        List<Trip> trips = tripRepository.findAllByVehicle_IdAndDateFromGreaterThanEqualAndDateToLessThanEqual(
                vehicle.getId(),
                dateFrom,
                dateTo);

        if (CollectionUtils.isEmpty(trips)) {
            return List.of();
        }

        return vehicleLocationService.getAllGeoJsonByTrips(vehicle.getId(), trips);
    }
}

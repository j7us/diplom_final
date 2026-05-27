package org.example.application.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.model.Trip;
import org.example.model.Vehicle;
import org.example.model.VehicleLocation;
import org.example.application.repository.VehicleLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleLocationService {
    private final VehicleLocationRepository vehicleLocationRepository;
    private final VehicleService vehicleService;

    @Transactional
    public VehicleLocation create(UUID vehicleId,
                                  VehicleLocation location,
                                  String username) {
        Vehicle vehicle = vehicleService.getById(vehicleId, username);

        return createLocation(vehicle.getId(), location);
    }

    @Transactional
    public VehicleLocation create(UUID vehicleId,
                                  VehicleLocation location) {
        Vehicle vehicle = vehicleService.getById(vehicleId);

        return createLocation(vehicle.getId(), location);
    }

    public List<VehicleLocation> getAll(UUID vehicleId,
                                        LocalDateTime dateFrom,
                                        LocalDateTime dateTo,
                                        String username) {
        Vehicle vehicle = vehicleService.getById(vehicleId, username);

        return vehicleLocationRepository.findAllByVehicleIdAndDateBetween(vehicle.getId(), dateFrom, dateTo);
    }

    public List<VehicleLocation> getAllByTrips(UUID vehicleId, List<Trip> trips) {
        if (CollectionUtils.isEmpty(trips)) {
            return List.of();
        }

        return vehicleLocationRepository.findAllByVehicleIdAndTrips(vehicleId, trips);
    }

    public VehicleLocation getByVehicleIdAndDate(UUID vehicleId, Instant date) {
        return vehicleLocationRepository.findByVehicleIdAndDate(vehicleId, date)
                .orElseThrow(() -> new RuntimeException("Не найдена точка автомобиля на дату " + date));
    }

    @Transactional
    public void createAll(List<VehicleLocation> locations) {
        vehicleLocationRepository.saveAll(locations);
    }

    private VehicleLocation createLocation(UUID vehicleId, VehicleLocation location) {
        location.setId(UUID.randomUUID());
        location.setVehicleId(vehicleId);

        return vehicleLocationRepository.save(location);
    }
}

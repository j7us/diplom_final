package org.example.application.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.model.Trip;
import org.example.model.VehicleLocation;

public interface VehicleLocationRepository {
    VehicleLocation save(VehicleLocation location);

    List<VehicleLocation> saveAll(List<VehicleLocation> locations);

    List<VehicleLocation> findAllByVehicleIdAndDateBetween(UUID vehicleId,
                                                           LocalDateTime dateFrom,
                                                           LocalDateTime dateTo);

    List<VehicleLocation> findAllByVehicleIdAndTrips(UUID vehicleId, List<Trip> trips);

    Optional<VehicleLocation> findByVehicleIdAndDate(UUID vehicleId, Instant date);
}

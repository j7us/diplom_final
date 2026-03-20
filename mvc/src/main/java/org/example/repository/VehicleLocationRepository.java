package org.example.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.entity.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, UUID> {
    List<VehicleLocation> findAllByVehicle_IdAndDateBetween(UUID vehicleId,
                                                            LocalDateTime dateFrom,
                                                            LocalDateTime dateTo);
}

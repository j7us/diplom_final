package org.example.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.entity.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, UUID>,
        JpaSpecificationExecutor<VehicleLocation> {
    List<VehicleLocation> findAllByVehicle_IdAndDateBetween(UUID vehicleId,
                                                            LocalDateTime dateFrom,
                                                            LocalDateTime dateTo);
}

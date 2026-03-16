package org.example.repository;

import java.util.UUID;
import org.example.entity.DriverVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverVehicleRepository extends JpaRepository<DriverVehicle, UUID> {
    void deleteAllByVehicle_Id(UUID vehicleId);
}

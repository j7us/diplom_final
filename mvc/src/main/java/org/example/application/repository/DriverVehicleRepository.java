package org.example.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.model.DriverVehicle;

public interface DriverVehicleRepository {
    List<DriverVehicle> findAllByVehicleId(UUID vehicleId);

    Optional<DriverVehicle> findByVehicleIdAndDriverId(UUID vehicleId, UUID driverId);

    DriverVehicle save(DriverVehicle driverVehicle);

    List<DriverVehicle> saveAll(List<DriverVehicle> driverVehicles);
}

package org.example.adapter.repository.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.adapter.repository.entity.DriverVehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverVehicleRepository extends JpaRepository<DriverVehicleEntity, UUID> {
    List<DriverVehicleEntity> findAllByVehicleEntity_Id(UUID vehicleId);

    Optional<DriverVehicleEntity> findByVehicleEntity_IdAndDriverEntity_Id(UUID vehicleId, UUID driverId);
}

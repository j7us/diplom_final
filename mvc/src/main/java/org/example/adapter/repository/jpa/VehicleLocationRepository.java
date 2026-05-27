package org.example.adapter.repository.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.adapter.repository.entity.VehicleLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleLocationRepository extends JpaRepository<VehicleLocationEntity, UUID>,
        JpaSpecificationExecutor<VehicleLocationEntity> {
    List<VehicleLocationEntity> findAllByVehicleEntity_IdAndDateBetween(UUID vehicleId,
                                                                        LocalDateTime dateFrom,
                                                                        LocalDateTime dateTo);

    Optional<VehicleLocationEntity> findByVehicleEntity_IdAndDate(UUID vehicleId, LocalDateTime date);
}

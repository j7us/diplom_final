package org.example.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.example.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findAllByEnterprise_IdIn(List<UUID> enterpriseIds);

    Page<Vehicle> findAllByEnterprise_IdIn(List<UUID> enterpriseIds, Pageable pageable);

    Page<Vehicle> findAllByEnterprise_Id(UUID enterpriseId, Pageable pageable);

    List<Vehicle> findAllByEnterprise_IdAndProductionDateGreaterThanEqualAndProductionDateLessThanEqual(UUID enterpriseId,
                                                                                                          Instant dateFrom,
                                                                                                          Instant dateTo);
}

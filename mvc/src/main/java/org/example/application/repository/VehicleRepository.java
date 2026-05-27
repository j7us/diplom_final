package org.example.application.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.model.Vehicle;

public interface VehicleRepository {
    List<Vehicle> findAll();

    List<Vehicle> findAllByEnterpriseIds(List<UUID> enterpriseIds);

    List<Vehicle> findAllByEnterpriseIds(List<UUID> enterpriseIds, Integer pageNumber, Integer pageSize);

    Long countByEnterpriseIds(List<UUID> enterpriseIds);

    List<Vehicle> findAllByEnterpriseId(UUID enterpriseId, Integer pageNumber, Integer pageSize);

    Long countByEnterpriseId(UUID enterpriseId);

    Optional<Vehicle> findById(UUID id);

    List<Vehicle> findAllByEnterpriseIdAndProductionDateBetween(UUID enterpriseId,
                                                                Instant dateFrom,
                                                                Instant dateTo);

    Vehicle save(Vehicle vehicle);

    void deleteById(UUID id);
}

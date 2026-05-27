package org.example.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.model.Driver;

public interface DriverRepository {
    List<Driver> findAllByEnterpriseIds(List<UUID> enterpriseIds);

    List<Driver> findAllByEnterpriseIds(List<UUID> enterpriseIds, Integer pageNumber, Integer pageSize);

    Long countByEnterpriseIds(List<UUID> enterpriseIds);

    List<Driver> findAllByEnterpriseId(UUID enterpriseId);

    List<Driver> findAllByEnterpriseId(UUID enterpriseId, Integer pageNumber, Integer pageSize);

    Long countByEnterpriseId(UUID enterpriseId);

    Optional<Driver> findById(UUID id);

    Driver save(Driver driver);
}

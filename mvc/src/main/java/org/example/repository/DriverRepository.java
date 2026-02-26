package org.example.repository;

import java.util.List;
import java.util.UUID;
import org.example.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    List<Driver> findAllByEnterprise_IdIn(List<UUID> enterpriseIds);
}

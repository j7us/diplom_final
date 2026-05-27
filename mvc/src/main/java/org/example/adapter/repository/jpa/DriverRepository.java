package org.example.adapter.repository.jpa;

import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.DriverEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<DriverEntity, UUID> {
    List<DriverEntity> findAllByEnterpriseEntity_IdIn(List<UUID> enterpriseIds);

    Page<DriverEntity> findAllByEnterpriseEntity_IdIn(List<UUID> enterpriseIds, Pageable pageable);

    Long countByEnterpriseEntity_IdIn(List<UUID> enterpriseIds);

    List<DriverEntity> findAllByEnterpriseEntity_Id(UUID enterpriseId);

    Page<DriverEntity> findAllByEnterpriseEntity_Id(UUID enterpriseId, Pageable pageable);

    Long countByEnterpriseEntity_Id(UUID enterpriseId);
}

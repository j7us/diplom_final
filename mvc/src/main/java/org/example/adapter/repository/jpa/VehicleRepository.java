package org.example.adapter.repository.jpa;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID> {
    @EntityGraph(attributePaths = {
            "brandEntity",
            "enterpriseEntity",
            "driverVehicleEntities",
            "driverVehicleEntities.driverEntity"
    })
    @Query("select distinct v from VehicleEntity v where v.enterpriseEntity.id in :enterpriseIds")
    List<VehicleEntity> findAllByEnterpriseEntity_IdIn(@Param("enterpriseIds") List<UUID> enterpriseIds);

    Page<VehicleEntity> findAllByEnterpriseEntity_IdIn(List<UUID> enterpriseIds, Pageable pageable);

    Long countByEnterpriseEntity_IdIn(List<UUID> enterpriseIds);

    Page<VehicleEntity> findAllByEnterpriseEntity_Id(UUID enterpriseId, Pageable pageable);

    Long countByEnterpriseEntity_Id(UUID enterpriseId);

    List<VehicleEntity> findAllByEnterpriseEntity_IdAndProductionDateGreaterThanEqualAndProductionDateLessThanEqual(UUID enterpriseId,
                                                                                                                    Instant dateFrom,
                                                                                                                    Instant dateTo);
}

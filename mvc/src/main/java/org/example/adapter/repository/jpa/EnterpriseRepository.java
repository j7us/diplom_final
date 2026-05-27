package org.example.adapter.repository.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.adapter.repository.entity.EnterpriseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<EnterpriseEntity, UUID> {
    List<EnterpriseEntity> findAllByManagerEntities_Id(UUID managerId);

    Optional<EnterpriseEntity> findByIdAndManagerEntities_Id(UUID id, UUID managerId);
}

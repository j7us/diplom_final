package org.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, UUID> {
    List<Enterprise> findAllByManagers_Id(UUID managerId);

    Optional<Enterprise> findByIdAndManagers_Id(UUID id, UUID managerId);
}

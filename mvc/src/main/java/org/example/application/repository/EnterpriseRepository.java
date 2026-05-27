package org.example.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.model.Enterprise;

public interface EnterpriseRepository {
    List<Enterprise> findAllByManagerId(UUID managerId);

    Optional<Enterprise> findByIdAndManagerId(UUID id, UUID managerId);

    Optional<Enterprise> findById(UUID id);

    Enterprise save(Enterprise enterprise);

    void deleteById(UUID id);
}

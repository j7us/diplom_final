package org.example.repository;

import java.util.UUID;
import org.example.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, UUID> {
}

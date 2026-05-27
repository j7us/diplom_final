package org.example.adapter.repository.jpa;

import java.util.UUID;
import org.example.adapter.repository.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<BrandEntity, UUID> {
}

package org.example.repository;

import java.util.UUID;
import org.example.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
}

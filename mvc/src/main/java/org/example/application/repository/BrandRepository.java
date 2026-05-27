package org.example.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.model.Brand;

public interface BrandRepository {
    List<Brand> findAll();

    Optional<Brand> findById(UUID id);

    Brand save(Brand brand);

    void deleteById(UUID id);
}

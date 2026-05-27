package org.example.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.adapter.repository.entity.BrandEntity;
import org.example.adapter.repository.mapper.BrandMapper;
import org.example.model.Brand;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrandRepositoryAdapter implements org.example.application.repository.BrandRepository {
    private final org.example.adapter.repository.jpa.BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toModel)
                .toList();
    }

    @Override
    public Optional<Brand> findById(UUID id) {
        return brandRepository.findById(id)
                .map(brandMapper::toModel);
    }

    @Override
    public Brand save(Brand brand) {
        BrandEntity brandEntity = getEntityForSave(brand);
        BrandEntity savedBrandEntity = brandRepository.save(brandEntity);

        return brandMapper.toModel(savedBrandEntity);
    }

    @Override
    public void deleteById(UUID id) {
        brandRepository.deleteById(id);
    }

    private BrandEntity getEntityForSave(Brand brand) {
        if (brand.getId() == null) {
            return brandMapper.toEntity(brand);
        }

        Optional<BrandEntity> savedBrand = brandRepository.findById(brand.getId());
        if (savedBrand.isEmpty()) {
            return brandMapper.toEntity(brand);
        }

        BrandEntity brandEntity = savedBrand.get();
        brandMapper.updateEntity(brand, brandEntity);

        return brandEntity;
    }
}

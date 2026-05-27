package org.example.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.model.Brand;
import org.example.application.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    public Brand getById(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Бренд не найден"));
    }

    @Transactional
    public Brand save(Brand brand) {
        brand.setId(UUID.randomUUID());

        return brandRepository.save(brand);
    }

    @Transactional
    public Brand update(UUID id, Brand brand) {
        Brand savedBrand = getById(id);

        updateBrand(brand, savedBrand);

        return brandRepository.save(savedBrand);
    }

    @Transactional
    public void delete(UUID id) {
        brandRepository.deleteById(id);
    }

    private void updateBrand(Brand source, Brand target) {
        target.setName(source.getName());
        target.setType(source.getType());
        target.setCapacity(source.getCapacity());
        target.setDrive(source.getDrive());
        target.setWeight(source.getWeight());
    }
}

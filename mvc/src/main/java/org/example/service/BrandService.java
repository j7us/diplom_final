package org.example.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.brand.BrandDto;
import org.example.dto.brand.BrandRestDto;
import org.example.entity.Brand;
import org.example.map.BrandMapper;
import org.example.map.BrandRestMapper;
import org.example.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final BrandRestMapper brandRestMapper;

    public List<BrandDto> getAll() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toDto)
                .toList();
    }

    public BrandDto getById(UUID id) {
        return brandMapper.toDto(brandRepository.findById(id).orElseThrow());
    }

    public List<BrandRestDto> getAllRest() {
        return brandRepository.findAll().stream()
                .map(brandRestMapper::toDto)
                .toList();
    }

    public BrandRestDto getRestById(UUID id) {
        return brandRestMapper.toDto(brandRepository.findById(id).orElseThrow());
    }

    public Brand getEntityById(UUID id) {
        return brandRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void save(BrandDto dto) {
        Brand brand = brandMapper.toEntity(dto);
        brand.setId(UUID.randomUUID());
        brandMapper.toDto(brandRepository.save(brand));
    }

    @Transactional
    public void update(UUID id, BrandDto dto) {
        Brand brand = brandRepository.findById(id).orElseThrow();
        brandMapper.updateEntity(dto, brand);
        brandMapper.toDto(brandRepository.save(brand));
    }

    @Transactional
    public void delete(UUID id) {
        brandRepository.deleteById(id);
    }
}

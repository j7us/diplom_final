package org.example.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.BrandDto;
import org.example.map.BrandMapper;
import org.example.repository.BrandRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public List<BrandDto> getAll() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toDto)
                .toList();
    }

    public BrandDto getById(UUID id) {
        return brandMapper.toDto(brandRepository.findById(id).orElseThrow());
    }

    public void save(BrandDto dto) {
        var brand = brandMapper.toEntity(dto);
        brand.setId(UUID.randomUUID());
        brandMapper.toDto(brandRepository.save(brand));
    }

    public void update(UUID id, BrandDto dto) {
        var brand = brandRepository.findById(id).orElseThrow();
        brandMapper.updateEntity(dto, brand);
        brandMapper.toDto(brandRepository.save(brand));
    }

    public void delete(UUID id) {
        brandRepository.deleteById(id);
    }
}

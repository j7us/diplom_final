package org.example.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.DriverRestDto;
import org.example.entity.Driver;
import org.example.map.DriverRestMapper;
import org.example.repository.DriverRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverRestMapper driverRestMapper;

    public List<DriverRestDto> getAll() {
        return driverRepository.findAll().stream()
                .map(driverRestMapper::toDto)
                .toList();
    }

    public DriverRestDto getById(UUID id) {
        Driver driver = driverRepository.findById(id).orElseThrow();

        return driverRestMapper.toDto(driver);
    }
}

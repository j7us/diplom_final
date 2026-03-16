package org.example.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.DriverRestDto;
import org.example.dto.driver.DriverCreateDto;
import org.example.entity.Driver;
import org.example.map.DriverRestMapper;
import org.example.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverRestMapper driverRestMapper;
    private final EnterpriseService enterpriseService;

    public List<DriverRestDto> getAll(String username) {
        List<UUID> enterpriseIds = enterpriseService.getEnterpriseIdsByManagerUsername(username);

        if (enterpriseIds.isEmpty()) {
            return List.of();
        }

        return driverRepository.findAllByEnterprise_IdIn(enterpriseIds).stream()
                .map(driverRestMapper::toDto)
                .toList();
    }

    public DriverRestDto getById(UUID id, String username) {
        Driver driver = driverRepository.findById(id).orElseThrow();

        enterpriseService.getByIdAndManagerUsername(driver.getEnterprise().getId(), username);

        return driverRestMapper.toDto(driver);
    }

    public Driver getEntityById(UUID id) {
        return driverRepository.findById(id).orElseThrow();
    }

    @Transactional
    public DriverRestDto create(DriverCreateDto dto) {
        Driver driver = driverRestMapper.toEntity(dto);

        driver.setId(UUID.randomUUID());
        driver.setEnterprise(enterpriseService.getEntityById(dto.getEnterpriseId()));

        Driver savedDriver = driverRepository.save(driver);

        return driverRestMapper.toDto(savedDriver);
    }
}

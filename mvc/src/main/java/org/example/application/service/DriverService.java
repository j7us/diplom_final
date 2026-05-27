package org.example.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.model.Enterprise;
import org.example.model.Driver;
import org.example.application.repository.DriverRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {
    private final DriverRepository driverRepository;
    private final EnterpriseService enterpriseService;

    public List<Driver> getAll(String username) {
        List<UUID> enterpriseIds = enterpriseService.getEnterpriseIdsByManagerUsername(username);

        if (enterpriseIds.isEmpty()) {
            return List.of();
        }

        return driverRepository.findAllByEnterpriseIds(enterpriseIds);
    }

    public Page<Driver> getAll(String username, Pageable pageable) {
        List<UUID> enterpriseIds = enterpriseService.getEnterpriseIdsByManagerUsername(username);

        if (enterpriseIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Driver> drivers = driverRepository.findAllByEnterpriseIds(
                enterpriseIds,
                pageable.getPageNumber(),
                pageable.getPageSize());
        Long count = driverRepository.countByEnterpriseIds(enterpriseIds);

        return new PageImpl<>(drivers, pageable, count);
    }

    public List<Driver> getAllByEnterprise(String username, UUID enterpriseId) {
        enterpriseService.getById(enterpriseId, username);

        return driverRepository.findAllByEnterpriseId(enterpriseId);
    }

    public Page<Driver> getAllByEnterprise(String username, UUID enterpriseId, Pageable pageable) {
        enterpriseService.getById(enterpriseId, username);

        List<Driver> drivers = driverRepository.findAllByEnterpriseId(
                enterpriseId,
                pageable.getPageNumber(),
                pageable.getPageSize());
        Long count = driverRepository.countByEnterpriseId(enterpriseId);

        return new PageImpl<>(drivers, pageable, count);
    }

    public Driver getById(UUID id, String username) {
        Driver driver = getById(id);

        enterpriseService.getById(driver.getEnterpriseId(), username);

        return driver;
    }

    public Driver getById(UUID id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Водитель не найден"));
    }

    @Transactional
    public Driver create(Driver driver) {
        Enterprise enterprise = enterpriseService.getById(driver.getEnterpriseId());

        driver.setId(UUID.randomUUID());
        driver.setEnterpriseId(enterprise.getId());

        return driverRepository.save(driver);
    }
}

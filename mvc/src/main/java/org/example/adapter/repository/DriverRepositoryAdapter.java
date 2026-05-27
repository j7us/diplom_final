package org.example.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.adapter.repository.entity.DriverEntity;
import org.example.adapter.repository.mapper.DriverMapper;
import org.example.model.Driver;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverRepositoryAdapter implements org.example.application.repository.DriverRepository {
    private final org.example.adapter.repository.jpa.DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    public List<Driver> findAllByEnterpriseIds(List<UUID> enterpriseIds) {
        return driverMapper.toModel(driverRepository.findAllByEnterpriseEntity_IdIn(enterpriseIds));
    }

    @Override
    public List<Driver> findAllByEnterpriseIds(List<UUID> enterpriseIds, Integer pageNumber, Integer pageSize) {
        return driverRepository.findAllByEnterpriseEntity_IdIn(enterpriseIds, PageRequest.of(pageNumber, pageSize))
                .map(driverMapper::toModel)
                .toList();
    }

    @Override
    public Long countByEnterpriseIds(List<UUID> enterpriseIds) {
        return driverRepository.countByEnterpriseEntity_IdIn(enterpriseIds);
    }

    @Override
    public List<Driver> findAllByEnterpriseId(UUID enterpriseId) {
        return driverMapper.toModel(driverRepository.findAllByEnterpriseEntity_Id(enterpriseId));
    }

    @Override
    public List<Driver> findAllByEnterpriseId(UUID enterpriseId, Integer pageNumber, Integer pageSize) {
        return driverRepository.findAllByEnterpriseEntity_Id(enterpriseId, PageRequest.of(pageNumber, pageSize))
                .map(driverMapper::toModel)
                .toList();
    }

    @Override
    public Long countByEnterpriseId(UUID enterpriseId) {
        return driverRepository.countByEnterpriseEntity_Id(enterpriseId);
    }

    @Override
    public Optional<Driver> findById(UUID id) {
        return driverRepository.findById(id)
                .map(driverMapper::toModel);
    }

    @Override
    public Driver save(Driver driver) {
        DriverEntity driverEntity = driverMapper.toEntity(driver);
        DriverEntity savedDriverEntity = driverRepository.save(driverEntity);

        return driverMapper.toModel(savedDriverEntity);
    }
}

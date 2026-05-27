package org.example.adapter.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.adapter.repository.entity.VehicleEntity;
import org.example.adapter.repository.mapper.VehicleMapper;
import org.example.model.Vehicle;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleRepositoryAdapter implements org.example.application.repository.VehicleRepository {
    private final org.example.adapter.repository.jpa.VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    public List<Vehicle> findAll() {
        return vehicleMapper.toModel(vehicleRepository.findAll());
    }

    @Override
    public List<Vehicle> findAllByEnterpriseIds(List<UUID> enterpriseIds) {
        return vehicleMapper.toModel(vehicleRepository.findAllByEnterpriseEntity_IdIn(enterpriseIds));
    }

    @Override
    public List<Vehicle> findAllByEnterpriseIds(List<UUID> enterpriseIds, Integer pageNumber, Integer pageSize) {
        return vehicleRepository.findAllByEnterpriseEntity_IdIn(enterpriseIds, PageRequest.of(pageNumber, pageSize))
                .map(vehicleMapper::toModel)
                .toList();
    }

    @Override
    public Long countByEnterpriseIds(List<UUID> enterpriseIds) {
        return vehicleRepository.countByEnterpriseEntity_IdIn(enterpriseIds);
    }

    @Override
    public List<Vehicle> findAllByEnterpriseId(UUID enterpriseId, Integer pageNumber, Integer pageSize) {
        return vehicleRepository.findAllByEnterpriseEntity_Id(enterpriseId, PageRequest.of(pageNumber, pageSize))
                .map(vehicleMapper::toModel)
                .toList();
    }

    @Override
    public Long countByEnterpriseId(UUID enterpriseId) {
        return vehicleRepository.countByEnterpriseEntity_Id(enterpriseId);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toModel);
    }

    @Override
    public List<Vehicle> findAllByEnterpriseIdAndProductionDateBetween(UUID enterpriseId,
                                                                       Instant dateFrom,
                                                                       Instant dateTo) {
        return vehicleMapper.toModel(
                vehicleRepository.findAllByEnterpriseEntity_IdAndProductionDateGreaterThanEqualAndProductionDateLessThanEqual(
                        enterpriseId,
                        dateFrom,
                        dateTo));
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity vehicleEntity = getEntityForSave(vehicle);
        VehicleEntity savedVehicleEntity = vehicleRepository.save(vehicleEntity);

        return vehicleMapper.toModel(savedVehicleEntity);
    }

    @Override
    public void deleteById(UUID id) {
        vehicleRepository.deleteById(id);
    }

    private VehicleEntity getEntityForSave(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            return vehicleMapper.toEntity(vehicle);
        }

        Optional<VehicleEntity> savedVehicle = vehicleRepository.findById(vehicle.getId());
        if (savedVehicle.isEmpty()) {
            return vehicleMapper.toEntity(vehicle);
        }

        VehicleEntity vehicleEntity = savedVehicle.get();
        vehicleMapper.updateEntity(vehicle, vehicleEntity);
        vehicleEntity.setBrandEntity(vehicleMapper.toBrandEntity(vehicle.getBrandId()));
        vehicleEntity.setEnterpriseEntity(vehicleMapper.toEnterpriseEntity(vehicle.getEnterpriseId()));

        return vehicleEntity;
    }
}

package org.example.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.adapter.repository.mapper.DriverVehicleMapper;
import org.example.model.DriverVehicle;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverVehicleRepositoryAdapter implements org.example.application.repository.DriverVehicleRepository {
    private final org.example.adapter.repository.jpa.DriverVehicleRepository driverVehicleRepository;
    private final DriverVehicleMapper driverVehicleMapper;

    @Override
    public List<DriverVehicle> findAllByVehicleId(UUID vehicleId) {
        return driverVehicleMapper.toModel(driverVehicleRepository.findAllByVehicleEntity_Id(vehicleId));
    }

    @Override
    public Optional<DriverVehicle> findByVehicleIdAndDriverId(UUID vehicleId, UUID driverId) {
        return driverVehicleRepository.findByVehicleEntity_IdAndDriverEntity_Id(vehicleId, driverId)
                .map(driverVehicleMapper::toModel);
    }

    @Override
    public DriverVehicle save(DriverVehicle driverVehicle) {
        return driverVehicleMapper.toModel(driverVehicleRepository.save(driverVehicleMapper.toEntity(driverVehicle)));
    }

    @Override
    public List<DriverVehicle> saveAll(List<DriverVehicle> driverVehicles) {
        return driverVehicleMapper.toModel(driverVehicleRepository.saveAll(
                driverVehicles.stream()
                        .map(driverVehicleMapper::toEntity)
                        .toList()));
    }
}

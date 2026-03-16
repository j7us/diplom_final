package org.example.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.driver.DriverVehicleCreateDto;
import org.example.entity.DriverVehicle;
import org.example.map.DriverVehicleMapper;
import org.example.repository.DriverVehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverVehicleService {
    private final DriverVehicleRepository driverVehicleRepository;
    private final DriverVehicleMapper driverVehicleMapper;
    private final DriverService driverService;
    private final VehicleService vehicleService;

    @Transactional
    public void create(DriverVehicleCreateDto dto) {
        DriverVehicle driverVehicle = driverVehicleMapper.toEntity(dto);

        driverVehicle.setId(UUID.randomUUID());
        driverVehicle.setDriver(driverService.getEntityById(dto.getDriverId()));
        driverVehicle.setVehicle(vehicleService.getEntityById(dto.getVehicleId()));

        driverVehicleRepository.save(driverVehicle);
    }
}

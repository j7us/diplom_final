package org.example.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.model.DriverVehicle;
import org.example.application.repository.DriverVehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverVehicleService {
    private final DriverService driverService;
    private final DriverVehicleRepository driverVehicleRepository;

    @Transactional
    public void setActiveDriver(UUID vehicleId, UUID driverId) {
        driverService.getById(driverId);

        List<DriverVehicle> driversForVehicle = driverVehicleRepository.findAllByVehicleId(vehicleId);
        DriverVehicle driverVehicle = findDriverVehicle(driversForVehicle, driverId);

        if (driverVehicle != null && Boolean.TRUE.equals(driverVehicle.getActive())) {
            return;
        }

        deactivateOtherDrivers(driversForVehicle);

        if (driverVehicle == null) {
            driverVehicleRepository.save(create(vehicleId, driverId));
            return;
        }

        driverVehicle.setActive(true);
        driverVehicleRepository.save(driverVehicle);
    }

    @Transactional
    public void deactivateDrivers(UUID vehicleId) {
        List<DriverVehicle> driversForVehicle = driverVehicleRepository.findAllByVehicleId(vehicleId);

        if (CollectionUtils.isEmpty(driversForVehicle)) {
            return;
        }

        driversForVehicle.forEach(driverVehicle -> driverVehicle.setActive(false));
        driverVehicleRepository.saveAll(driversForVehicle);
    }

    private DriverVehicle findDriverVehicle(List<DriverVehicle> driversForVehicle, UUID driverId) {
        return driversForVehicle.stream()
                .filter(driverVehicle -> driverVehicle.getDriverId().equals(driverId))
                .findFirst()
                .orElse(null);
    }

    private void deactivateOtherDrivers(List<DriverVehicle> driversForVehicle) {
        if (CollectionUtils.isEmpty(driversForVehicle)) {
            return;
        }

        driversForVehicle.forEach(d -> d.setActive(false));
        driverVehicleRepository.saveAll(driversForVehicle);
    }

    public DriverVehicle create(UUID vehicleId, UUID driverId) {
        return DriverVehicle.builder()
                .id(UUID.randomUUID())
                .vehicleId(vehicleId)
                .driverId(driverId)
                .active(true)
                .build();
    }
}

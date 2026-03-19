package org.example.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.entity.DriverVehicle;
import org.example.entity.Vehicle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverVehicleService {
    private final DriverService driverService;

    @Transactional
    public void setActiveDriver(Vehicle vehicle, UUID driverId) {
        List<DriverVehicle> driversForVehicle = vehicle.getDriverVehicles();

        DriverVehicle driverVehicle = findDriverVehicle(driversForVehicle, driverId);

        if (driverVehicle != null && driverVehicle.getActive()) {
            return;
        }

        deactivateOtherDrivers(driversForVehicle);

        if (driverVehicle == null) {
            DriverVehicle newDriverVehicle = create(vehicle, driverId);
            newDriverVehicle.setActive(true);
            driversForVehicle.add(newDriverVehicle);
            return;
        }

        driverVehicle.setActive(true);
    }

    @Transactional
    public void deactivateDrivers(Vehicle vehicle) {
        List<DriverVehicle> driversForVehicle = vehicle.getDriverVehicles();

        if (CollectionUtils.isEmpty(driversForVehicle)) {
            return;
        }

        driversForVehicle.forEach(driverVehicle -> driverVehicle.setActive(false));
    }

    private DriverVehicle findDriverVehicle(List<DriverVehicle> driversForVehicle, UUID driverId) {
        return driversForVehicle.stream()
                .filter(driverVehicle -> driverVehicle.getDriver().getId().equals(driverId))
                .findFirst()
                .orElse(null);
    }

    private void deactivateOtherDrivers(List<DriverVehicle> driversForVehicle) {
        driversForVehicle.forEach(d -> d.setActive(false));
    }

    public DriverVehicle create(Vehicle vehicle, UUID driverId) {
        DriverVehicle driverVehicle = new DriverVehicle();

        driverVehicle.setId(UUID.randomUUID());
        driverVehicle.setDriver(driverService.getEntityById(driverId));
        driverVehicle.setVehicle(vehicle);

        return driverVehicle;
    }
}

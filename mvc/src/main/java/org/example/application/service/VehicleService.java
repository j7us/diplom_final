package org.example.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.model.Enterprise;
import org.example.model.Vehicle;
import org.example.application.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final BrandService brandService;
    private final EnterpriseService enterpriseService;
    private final DriverVehicleService driverVehicleService;

    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getAll(String username) {
        List<UUID> enterpriseIds = enterpriseService.getEnterpriseIdsByManagerUsername(username);

        if (enterpriseIds.isEmpty()) {
            return List.of();
        }

        return vehicleRepository.findAllByEnterpriseIds(enterpriseIds);
    }

    public Page<Vehicle> getAll(String username, Pageable pageable) {
        List<UUID> enterpriseIds = enterpriseService.getEnterpriseIdsByManagerUsername(username);

        if (enterpriseIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Vehicle> vehicles = vehicleRepository.findAllByEnterpriseIds(
                enterpriseIds,
                pageable.getPageNumber(),
                pageable.getPageSize());
        Long count = vehicleRepository.countByEnterpriseIds(enterpriseIds);

        return new PageImpl<>(vehicles, pageable, count);
    }

    public Page<Vehicle> getAllByEnterprise(String username, UUID enterpriseId, Pageable pageable) {
        enterpriseService.getById(enterpriseId, username);

        List<Vehicle> vehicles = vehicleRepository.findAllByEnterpriseId(
                enterpriseId,
                pageable.getPageNumber(),
                pageable.getPageSize());
        Long count = vehicleRepository.countByEnterpriseId(enterpriseId);

        return new PageImpl<>(vehicles, pageable, count);
    }

    public Vehicle getById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
    }

    public Vehicle getById(UUID id, String username) {
        Vehicle vehicle = getById(id);

        enterpriseService.getById(vehicle.getEnterpriseId(), username);

        return vehicle;
    }

    public List<Vehicle> getByEnterpriseAndManagerUsernameAndProductionDateBetween(UUID enterpriseId,
                                                                                   Instant dateFrom,
                                                                                   Instant dateTo,
                                                                                   String username) {
        Enterprise enterprise = enterpriseService.getById(enterpriseId, username);

        return vehicleRepository.findAllByEnterpriseIdAndProductionDateBetween(
                enterprise.getId(),
                dateFrom,
                dateTo);
    }

    @Transactional
    public Vehicle createWithoutEnterprise(Vehicle vehicle) {
        vehicle.setId(UUID.randomUUID());
        validateBrand(vehicle.getBrandId());

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle create(Vehicle vehicle, String username) {
        Enterprise enterprise = enterpriseService.getById(vehicle.getEnterpriseId(), username);

        vehicle.setEnterpriseId(enterprise.getId());

        return createWithEnterprise(vehicle);
    }

    @Transactional
    public Vehicle createWithoutUsername(Vehicle vehicle) {
        Enterprise enterprise = enterpriseService.getById(vehicle.getEnterpriseId());

        vehicle.setEnterpriseId(enterprise.getId());

        return createWithEnterprise(vehicle);
    }

    @Transactional
    public Vehicle update(UUID id, Vehicle vehicle) {
        Vehicle savedVehicle = getById(id);

        updateVehicle(vehicle, savedVehicle);
        validateBrand(savedVehicle.getBrandId());

        return vehicleRepository.save(savedVehicle);
    }

    @Transactional
    public Vehicle update(UUID id, Vehicle vehicle, String username) {
        Vehicle savedVehicle = getById(id, username);

        updateVehicle(vehicle, savedVehicle);
        validateBrand(savedVehicle.getBrandId());

        Vehicle updatedVehicle = vehicleRepository.save(savedVehicle);
        updateActiveDriver(updatedVehicle.getId(), vehicle.getActiveDriverId());

        return getById(updatedVehicle.getId());
    }

    @Transactional
    public void delete(UUID id, String username) {
        Vehicle vehicle = getById(id, username);

        vehicleRepository.deleteById(vehicle.getId());
    }

    @Transactional
    public void delete(UUID id) {
        vehicleRepository.deleteById(id);
    }

    private Vehicle createWithEnterprise(Vehicle vehicle) {
        vehicle.setId(UUID.randomUUID());
        validateBrand(vehicle.getBrandId());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        if (vehicle.getActiveDriverId() == null) {
            return savedVehicle;
        }

        driverVehicleService.setActiveDriver(savedVehicle.getId(), vehicle.getActiveDriverId());

        return getById(savedVehicle.getId());
    }

    private void updateVehicle(Vehicle source, Vehicle target) {
        target.setMilleage(source.getMilleage());
        target.setPrice(source.getPrice());
        target.setCountry(source.getCountry());
        target.setProductionDate(source.getProductionDate());
        target.setBrandId(source.getBrandId());
    }

    private void validateBrand(UUID brandId) {
        if (brandId == null) {
            return;
        }

        brandService.getById(brandId);
    }

    private void updateActiveDriver(UUID vehicleId, UUID activeDriverId) {
        if (activeDriverId == null) {
            driverVehicleService.deactivateDrivers(vehicleId);
            return;
        }

        driverVehicleService.setActiveDriver(vehicleId, activeDriverId);
    }
}

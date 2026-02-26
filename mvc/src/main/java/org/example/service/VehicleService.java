package org.example.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.VehicleDto;
import org.example.dto.VehicleRestDto;
import org.example.entity.Brand;
import org.example.entity.Vehicle;
import org.example.map.VehicleMapper;
import org.example.map.VehicleRestMapper;
import org.example.repository.BrandRepository;
import org.example.repository.DriverVehicleRepository;
import org.example.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final BrandRepository brandRepository;
    private final DriverVehicleRepository driverVehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final VehicleRestMapper vehicleRestMapper;

    public List<VehicleDto> getAll() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toDto)
                .toList();
    }

    public VehicleDto getById(UUID id) {
        return vehicleMapper.toDto(vehicleRepository.findById(id).orElseThrow());
    }

    public List<VehicleRestDto> getAllWithBrandIdOnly() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        return vehicles.stream()
                .map(vehicleRestMapper::toDto)
                .toList();
    }

    public VehicleRestDto getByIdWithBrandIdOnly(UUID id) {
        return vehicleRestMapper.toDto(vehicleRepository.findById(id).orElseThrow());
    }

    @Transactional
    public void create(VehicleDto dto) {
        Vehicle vehicle = vehicleMapper.toEntity(dto);

        vehicle.setId(UUID.randomUUID());
        vehicle.setBrand(resolveBrand(dto.brandId()));

        vehicleMapper.toDto(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void update(UUID id, VehicleDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();

        vehicleMapper.updateEntity(dto, vehicle);
        vehicle.setBrand(resolveBrand(dto.brandId()));

        vehicleMapper.toDto(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(UUID id) {
        vehicleRepository.deleteById(id);
    }

    private Brand resolveBrand(String brandId) {
        if (brandId == null || brandId.isBlank()) {
            return null;
        }
        return brandRepository.findById(UUID.fromString(brandId)).orElseThrow();
    }
}

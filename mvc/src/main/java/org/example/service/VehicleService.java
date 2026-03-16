package org.example.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.vehicle.VehicleCreateRestDto;
import org.example.dto.vehicle.VehicleDto;
import org.example.dto.vehicle.VehicleRestDto;
import org.example.entity.Brand;
import org.example.entity.Enterprise;
import org.example.entity.Vehicle;
import org.example.map.VehicleMapper;
import org.example.map.VehicleRestMapper;
import org.example.repository.BrandRepository;
import org.example.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final BrandRepository brandRepository;
    private final BrandService brandService;
    private final EnterpriseService enterpriseService;
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

    public Vehicle getEntityById(UUID id) {
        return vehicleRepository.findById(id).orElseThrow();
    }

    public List<VehicleRestDto> getAllWithBrandIdOnly(String username) {
        List<UUID> enterpriseIds = enterpriseService.getEnterpriseIdsByManagerUsername(username);

        if (enterpriseIds.isEmpty()) {
            return List.of();
        }

        List<Vehicle> vehicles = vehicleRepository.findAllByEnterprise_IdIn(enterpriseIds);

        return vehicles.stream()
                .map(vehicleRestMapper::toDto)
                .toList();
    }

    public VehicleRestDto getByIdWithBrandIdOnly(UUID id, String username) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();

        enterpriseService.getByIdAndManagerUsername(vehicle.getEnterprise().getId(), username);

        return vehicleRestMapper.toDto(vehicle);
    }

    @Transactional
    public VehicleRestDto create(VehicleCreateRestDto dto, String username) {
        Enterprise entityByIdAndManagerUsername
                = enterpriseService.getEntityByIdAndManagerUsername(dto.getEnterpriseId(), username);

        Vehicle vehicle = vehicleRestMapper.toEntity(dto);
        vehicle.setId(UUID.randomUUID());
        vehicle.setBrand(brandService.getEntityById(dto.getBrandId()));
        vehicle.setEnterprise(entityByIdAndManagerUsername);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleRestMapper.toDto(savedVehicle);
    }

    @Transactional
    public VehicleRestDto create(VehicleCreateRestDto dto) {
        Enterprise enterprise = enterpriseService.getEntityById(dto.getEnterpriseId());

        Vehicle vehicle = vehicleRestMapper.toEntity(dto);
        vehicle.setId(UUID.randomUUID());
        vehicle.setBrand(resolveBrand(dto.getBrandId()));
        vehicle.setEnterprise(enterprise);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleRestMapper.toDto(savedVehicle);
    }

    @Transactional
    public VehicleRestDto update(UUID id, VehicleRestDto dto, String username) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();

        enterpriseService.getByIdAndManagerUsername(vehicle.getEnterprise().getId(), username);

        vehicleRestMapper.updateEntity(dto, vehicle);
        vehicle.setBrand(brandService.getEntityById(dto.getBrandId()));

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleRestMapper.toDto(savedVehicle);
    }

    @Transactional
    public void delete(UUID id, String username) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();
        enterpriseService.getEntityByIdAndManagerUsername(vehicle.getEnterprise().getId(), username);
        vehicleRepository.deleteById(id);
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

    private Brand resolveBrand(UUID brandId) {
        if (brandId == null) {
            return null;
        }
        return brandService.getEntityById(brandId);
    }
}

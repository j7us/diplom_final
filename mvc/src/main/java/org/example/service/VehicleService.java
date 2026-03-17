package org.example.service;

import java.util.ArrayList;
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
import org.example.repository.VehicleRepository;
import org.springframework.data.domain.Page;
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
    private final VehicleMapper vehicleMapper;
    private final VehicleRestMapper vehicleRestMapper;
    private final DriverVehicleService driverVehicleService;

    public List<VehicleDto> getAll() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toDto)
                .toList();
    }

    public List<VehicleRestDto> getAll(String username) {
        List<UUID> enterpriseIds = enterpriseService.getEnterpriseIdsByManagerUsername(username);

        if (enterpriseIds.isEmpty()) {
            return List.of();
        }

        return vehicleRepository.findAllByEnterprise_IdIn(enterpriseIds).stream()
                .map(vehicleRestMapper::toDto)
                .toList();
    }

    public Page<VehicleRestDto> getAll(String username, Pageable pageable) {
        List<UUID> enterpriseIds = enterpriseService.getEnterpriseIdsByManagerUsername(username);

        if (enterpriseIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return vehicleRepository.findAllByEnterprise_IdIn(enterpriseIds, pageable)
                .map(vehicleRestMapper::toDto);
    }

    public Page<VehicleRestDto> getAllByEnterprise(String username, UUID enterpriseId, Pageable pageable) {
        enterpriseService.getEntityByIdAndManagerUsername(enterpriseId, username);

        return vehicleRepository.findAllByEnterprise_Id(enterpriseId, pageable)
                .map(vehicleRestMapper::toDto);
    }

    public VehicleDto getById(UUID id) {
        return vehicleMapper.toDto(vehicleRepository.findById(id).orElseThrow());
    }

    public VehicleRestDto getById(UUID id, String username) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();

        enterpriseService.getByIdAndManagerUsername(vehicle.getEnterprise().getId(), username);

        return vehicleRestMapper.toDto(vehicle);
    }

    @Transactional
    public void createWithoutEnterprise(VehicleDto dto) {
        Vehicle vehicle = vehicleMapper.toEntity(dto);

        vehicle.setId(UUID.randomUUID());
        vehicle.setBrand(resolveBrand(dto.brandId()));

        vehicleRepository.save(vehicle);
    }

    @Transactional
    public VehicleRestDto create(VehicleCreateRestDto dto, String username) {
        Enterprise enterprise = enterpriseService.getEntityByIdAndManagerUsername(
                dto.getEnterpriseId(),
                username);

        return createWithEnterprise(dto, enterprise);
    }

    @Transactional
    public VehicleRestDto createWithoutUsername(VehicleCreateRestDto dto) {
        Enterprise enterprise = enterpriseService.getEntityById(dto.getEnterpriseId());

        return createWithEnterprise(dto, enterprise);
    }

    private VehicleRestDto createWithEnterprise(VehicleCreateRestDto dto, Enterprise enterprise) {
        Vehicle vehicle = vehicleRestMapper.toEntity(dto);
        vehicle.setId(UUID.randomUUID());
        vehicle.setBrand(resolveBrand(dto.getBrandId()));
        vehicle.setEnterprise(enterprise);

        if (dto.getActiveDriverId() != null) {
            vehicle.setDriverVehicles(new ArrayList<>());
            driverVehicleService.setActiveDriver(vehicle, dto.getActiveDriverId());
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleRestMapper.toDto(savedVehicle);
    }

    @Transactional
    public void update(UUID id, VehicleDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();

        vehicleMapper.updateEntity(dto, vehicle);
        vehicle.setBrand(resolveBrand(dto.brandId()));

        vehicleMapper.toDto(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleRestDto update(UUID id, VehicleRestDto dto, String username) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();

        enterpriseService.getByIdAndManagerUsername(vehicle.getEnterprise().getId(), username);

        vehicleRestMapper.updateEntity(dto, vehicle);
        vehicle.setBrand(brandService.getEntityById(dto.getBrandId()));

        if (dto.getActiveDriverId() != null) {
            driverVehicleService.setActiveDriver(vehicle, dto.getActiveDriverId());
        }

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
    public void delete(UUID id) {
        vehicleRepository.deleteById(id);
    }

    private Brand resolveBrand(UUID brandId) {
        if (brandId == null) {
            return null;
        }
        return brandService.getEntityById(brandId);
    }
}

package org.example.adapter.controller;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.controller.dto.brand.BrandRestDto;
import org.example.adapter.controller.dto.vehicle.VehicleCreateRestDto;
import org.example.adapter.controller.dto.vehicle.VehicleRestDto;
import org.example.adapter.controller.dto.vehicle.VehicleUpdateRestDto;
import org.example.adapter.controller.mapper.BrandRestMapper;
import org.example.adapter.controller.mapper.VehicleRestMapper;
import org.example.model.Vehicle;
import org.example.application.service.BrandService;
import org.example.application.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Автомобили", description = "Эндпоинты автомобилей")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class VehicleRestController {
    private final VehicleService vehicleService;
    private final BrandService brandService;
    private final VehicleRestMapper vehicleRestMapper;
    private final BrandRestMapper brandRestMapper;

    @GetMapping("/vehicles")
    public List<VehicleRestDto> getVehicles(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicles с параметрами: {}", userDetails.getUsername());

        return vehicleService.getAll(userDetails.getUsername()).stream()
                .map(vehicleRestMapper::toDto)
                .toList();
    }

    @GetMapping(value = "/vehicles", params = {"page", "size"})
    public Page<VehicleRestDto> getVehiclesPage(@AuthenticationPrincipal UserDetails userDetails,
                                                Pageable pageable,
                                                @RequestParam(required = false) UUID enterpriseId) {
        log.info("Пришел запрос /api/vehicles с параметрами: {}, {}, {}", pageable.getPageNumber(), pageable.getPageSize(), enterpriseId);

        return enterpriseId == null
                ? vehicleService.getAll(userDetails.getUsername(), pageable).map(vehicleRestMapper::toDto)
                : vehicleService.getAllByEnterprise(userDetails.getUsername(), enterpriseId, pageable).map(vehicleRestMapper::toDto);
    }

    @GetMapping("/brands")
    public List<BrandRestDto> getBrands() {
        log.info("Пришел запрос /api/brands");

        return brandService.getAll().stream()
                .map(brandRestMapper::toDto)
                .toList();
    }

    @GetMapping("/vehicle/{id}/")
    public VehicleRestDto getVehicle(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicle/{id}/ с параметрами: {}, {}", id, userDetails.getUsername());

        return vehicleRestMapper.toDto(vehicleService.getById(id, userDetails.getUsername()));
    }

    @GetMapping("/brands/{id}/")
    public BrandRestDto getBrand(@PathVariable UUID id) {
        log.info("Пришел запрос /api/brands/{id}/ с параметрами: {}", id);

        return brandRestMapper.toDto(brandService.getById(id));
    }

    @PostMapping("/vehicles")
    public VehicleRestDto createVehicle(@RequestBody VehicleCreateRestDto dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicles с параметрами: {}, {}, {}", dto.getEnterpriseId(), dto.getBrandId(), userDetails.getUsername());

        Vehicle vehicle = vehicleRestMapper.toModel(dto);
        Vehicle savedVehicle = vehicleService.create(vehicle, userDetails.getUsername());

        return vehicleRestMapper.toDto(savedVehicle);
    }

    @PutMapping("/vehicle/{id}/")
    public VehicleRestDto updateVehicle(@PathVariable UUID id,
                                        @RequestBody VehicleUpdateRestDto dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicle/{id}/ с параметрами: {}, {}, {}", id, dto.getBrandId(), userDetails.getUsername());

        Vehicle vehicle = vehicleRestMapper.toModel(dto);
        Vehicle savedVehicle = vehicleService.update(id, vehicle, userDetails.getUsername());

        return vehicleRestMapper.toDto(savedVehicle);
    }

    @DeleteMapping("/vehicle/{id}/")
    public void deleteVehicle(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/vehicle/{id}/ с параметрами: {}, {}", id, userDetails.getUsername());

        vehicleService.delete(id, userDetails.getUsername());
    }
}

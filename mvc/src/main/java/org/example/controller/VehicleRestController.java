package org.example.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.brand.BrandRestDto;
import org.example.dto.vehicle.VehicleCreateRestDto;
import org.example.dto.vehicle.VehicleRestDto;
import org.example.service.BrandService;
import org.example.service.VehicleService;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehicleRestController {
    private final VehicleService vehicleService;
    private final BrandService brandService;

    @GetMapping("/vehicles")
    public List<VehicleRestDto> getVehicles(@AuthenticationPrincipal UserDetails userDetails) {
        return vehicleService.getAll(userDetails.getUsername());
    }

    @GetMapping(value = "/vehicles", params = {"page", "size"})
    public Page<VehicleRestDto> getVehiclesPage(@AuthenticationPrincipal UserDetails userDetails,
                                                Pageable pageable,
                                                @RequestParam(required = false) UUID enterpriseId) {
        return enterpriseId == null
                ? vehicleService.getAll(userDetails.getUsername(), pageable)
                : vehicleService.getAllByEnterprise(userDetails.getUsername(), enterpriseId, pageable);
    }

    @GetMapping("/brands")
    public List<BrandRestDto> getBrands() {
        return brandService.getAllRest();
    }

    @GetMapping("/vehicle/{id}/")
    public VehicleRestDto getVehicle(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        return vehicleService.getById(id, userDetails.getUsername());
    }

    @GetMapping("/brand/{id}/")
    public BrandRestDto getBrand(@PathVariable UUID id) {
        return brandService.getRestById(id);
    }

    @PostMapping("/vehicles")
    public VehicleRestDto createVehicle(@RequestBody VehicleCreateRestDto dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return vehicleService.create(dto, userDetails.getUsername());
    }

    @PutMapping("/vehicle/{id}/")
    public VehicleRestDto updateVehicle(@PathVariable UUID id,
                                        @RequestBody VehicleRestDto dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return vehicleService.update(id, dto, userDetails.getUsername());
    }

    @DeleteMapping("/vehicle/{id}/")
    public void deleteVehicle(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        vehicleService.delete(id, userDetails.getUsername());
    }
}

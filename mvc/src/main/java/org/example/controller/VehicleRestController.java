package org.example.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.BrandRestDto;
import org.example.dto.VehicleRestDto;
import org.example.service.BrandService;
import org.example.service.VehicleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehicleRestController {
    private final VehicleService vehicleService;
    private final BrandService brandService;

    @GetMapping("/vehicles")
    public List<VehicleRestDto> getVehicles() {
        return vehicleService.getAllWithBrandIdOnly();
    }

    @GetMapping("/brands")
    public List<BrandRestDto> getBrands() {
        return brandService.getAllRest();
    }

    @GetMapping("/vehicle/{id}/")
    public VehicleRestDto getVehicle(@PathVariable UUID id) {
        return vehicleService.getByIdWithBrandIdOnly(id);
    }

    @GetMapping("/brand/{id}/")
    public BrandRestDto getBrand(@PathVariable UUID id) {
        return brandService.getRestById(id);
    }
}

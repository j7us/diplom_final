package org.example.adapter.controller;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.controller.dto.brand.BrandDto;
import org.example.adapter.controller.dto.vehicle.VehicleDto;
import org.example.adapter.controller.mapper.BrandMapper;
import org.example.adapter.controller.mapper.VehicleMapper;
import org.example.application.service.BrandService;
import org.example.application.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Hidden
@Controller
@RequiredArgsConstructor
@Slf4j
public class ModelVehicleController {
    private final VehicleService vehicleService;
    private final BrandService brandService;
    private final VehicleMapper vehicleMapper;
    private final BrandMapper brandMapper;

    @GetMapping("/view/vehicles")
    public String allInfo(Model model) {
        log.info("Пришел запрос /view/vehicles");

        model.addAttribute("vehicles", vehicleService.getAll().stream().map(vehicleMapper::toDto).toList());
        model.addAttribute("brands", brandService.getAll().stream().map(brandMapper::toDto).toList());
        return "model/vehicles";
    }

    @GetMapping("/view/vehicles/new")
    public String getVehicleCreateInfo(Model model) {
        log.info("Пришел запрос /view/vehicles/new");

        model.addAttribute("vehicle", new VehicleDto(null, null, null, null, null, null));
        model.addAttribute("brands", brandService.getAll().stream().map(brandMapper::toDto).toList());
        model.addAttribute("action", "/view/vehicles");
        model.addAttribute("title", "Добавить автомобиль");
        model.addAttribute("submitLabel", "Сохранить");
        return "model/vehicle-form";
    }

    @GetMapping("/view/vehicles/{id}/edit")
    public String editVehicle(@PathVariable UUID id, Model model) {
        log.info("Пришел запрос /view/vehicles/{id}/edit с параметрами: {}", id);

        model.addAttribute("vehicle", vehicleMapper.toDto(vehicleService.getById(id)));
        model.addAttribute("brands", brandService.getAll().stream().map(brandMapper::toDto).toList());
        model.addAttribute("action", "/view/vehicles/" + id);
        model.addAttribute("title", "Редактировать автомобиль");
        model.addAttribute("submitLabel", "Обновить");
        return "model/vehicle-form";
    }

    @PostMapping("/view/vehicles")
    public String saveVehicle(@ModelAttribute("vehicle") VehicleDto vehicle) {
        log.info("Пришел запрос /view/vehicles с параметрами: {}, {}, {}", vehicle.country(), vehicle.brandId(), vehicle.price());

        vehicleService.createWithoutEnterprise(vehicleMapper.toModel(vehicle));
        return "redirect:/view/vehicles";
    }

    @PostMapping("/view/vehicles/{id}")
    public String updateVehicle(@PathVariable UUID id, @ModelAttribute("vehicle") VehicleDto vehicle) {
        log.info("Пришел запрос /view/vehicles/{id} с параметрами: {}, {}, {}", id, vehicle.brandId(), vehicle.price());

        vehicleService.update(id, vehicleMapper.toModel(vehicle));
        return "redirect:/view/vehicles";
    }

    @PostMapping("/view/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable UUID id) {
        log.info("Пришел запрос /view/vehicles/{id}/delete с параметрами: {}", id);

        vehicleService.delete(id);
        return "redirect:/view/vehicles";
    }

    @GetMapping("/view/brands/new")
    public String getBrandCreateInfo(Model model) {
        log.info("Пришел запрос /view/brands/new");

        model.addAttribute("brand", new BrandDto(null, null, null, null, null, null));
        model.addAttribute("action", "/view/brands");
        model.addAttribute("title", "Добавить бренд");
        model.addAttribute("submitLabel", "Сохранить");
        return "model/brand-form";
    }

    @GetMapping("/view/brands/{id}/edit")
    public String editBrand(@PathVariable UUID id, Model model) {
        log.info("Пришел запрос /view/brands/{id}/edit с параметрами: {}", id);

        model.addAttribute("brand", brandMapper.toDto(brandService.getById(id)));
        model.addAttribute("action", "/view/brands/" + id);
        model.addAttribute("title", "Редактировать бренд");
        model.addAttribute("submitLabel", "Обновить");
        return "model/brand-form";
    }

    @PostMapping("/view/brands")
    public String saveBrand(@ModelAttribute("brand") BrandDto brand) {
        log.info("Пришел запрос /view/brands с параметрами: {}, {}, {}", brand.name(), brand.type(), brand.capacity());

        brandService.save(brandMapper.toModel(brand));
        return "redirect:/view/vehicles";
    }

    @PostMapping("/view/brands/{id}")
    public String updateBrand(@PathVariable UUID id, @ModelAttribute("brand") BrandDto brand) {
        log.info("Пришел запрос /view/brands/{id} с параметрами: {}, {}, {}", id, brand.name(), brand.type());

        brandService.update(id, brandMapper.toModel(brand));
        return "redirect:/view/vehicles";
    }

    @PostMapping("/view/brands/{id}/delete")
    public String deleteBrand(@PathVariable UUID id) {
        log.info("Пришел запрос /view/brands/{id}/delete с параметрами: {}", id);

        brandService.delete(id);
        return "redirect:/view/vehicles";
    }
}

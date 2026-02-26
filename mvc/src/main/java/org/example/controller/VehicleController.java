package org.example.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.BrandDto;
import org.example.dto.VehicleDto;
import org.example.service.BrandService;
import org.example.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;
    private final BrandService brandService;

    @GetMapping("/vehicles")
    public String allInfo(Model model) {
        model.addAttribute("vehicles", vehicleService.getAll());
        model.addAttribute("brands", brandService.getAll());
        return "vehicles";
    }

    @GetMapping("/vehicles/new")
    public String getVehicleCreateInfo(Model model) {
        model.addAttribute("vehicle", new VehicleDto(null, null, null, null, null, null));
        model.addAttribute("brands", brandService.getAll());
        model.addAttribute("action", "/vehicles");
        model.addAttribute("title", "Добавить автомобиль");
        model.addAttribute("submitLabel", "Сохранить");
        return "vehicle-form";
    }

    @GetMapping("/vehicles/{id}/edit")
    public String editVehicle(@PathVariable UUID id, Model model) {
        model.addAttribute("vehicle", vehicleService.getById(id));
        model.addAttribute("brands", brandService.getAll());
        model.addAttribute("action", "/vehicles/" + id);
        model.addAttribute("title", "Редактировать автомобиль");
        model.addAttribute("submitLabel", "Обновить");
        return "vehicle-form";
    }

    @PostMapping("/vehicles")
    public String saveVehicle(@ModelAttribute("vehicle") VehicleDto vehicle) {
        vehicleService.create(vehicle);
        return "redirect:/vehicles";
    }

    @PostMapping("/vehicles/{id}")
    public String updateVehicle(@PathVariable UUID id, @ModelAttribute("vehicle") VehicleDto vehicle) {
        vehicleService.update(id, vehicle);
        return "redirect:/vehicles";
    }

    @PostMapping("/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable UUID id) {
        vehicleService.delete(id);
        return "redirect:/vehicles";
    }

    @GetMapping("/brands/new")
    public String getBrandCreateInfo(Model model) {
        model.addAttribute("brand", new BrandDto(null, null, null, null, null, null));
        model.addAttribute("action", "/brands");
        model.addAttribute("title", "Добавить бренд");
        model.addAttribute("submitLabel", "Сохранить");
        return "brand-form";
    }

    @GetMapping("/brands/{id}/edit")
    public String editBrand(@PathVariable UUID id, Model model) {
        model.addAttribute("brand", brandService.getById(id));
        model.addAttribute("action", "/brands/" + id);
        model.addAttribute("title", "Редактировать бренд");
        model.addAttribute("submitLabel", "Обновить");
        return "brand-form";
    }

    @PostMapping("/brands")
    public String saveBrand(@ModelAttribute("brand") BrandDto brand) {
        brandService.save(brand);
        return "redirect:/vehicles";
    }

    @PostMapping("/brands/{id}")
    public String updateBrand(@PathVariable UUID id, @ModelAttribute("brand") BrandDto brand) {
        brandService.update(id, brand);
        return "redirect:/vehicles";
    }

    @PostMapping("/brands/{id}/delete")
    public String deleteBrand(@PathVariable UUID id) {
        brandService.delete(id);
        return "redirect:/vehicles";
    }
}

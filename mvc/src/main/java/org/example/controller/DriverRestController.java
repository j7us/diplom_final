package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.DriverRestDto;
import org.example.service.DriverService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DriverRestController {
    private final DriverService driverService;

    @GetMapping("/drivers")
    public List<DriverRestDto> getDrivers(@AuthenticationPrincipal UserDetails userDetails) {
        return driverService.getAll(userDetails.getUsername());
    }

    @GetMapping("/drivers/{id}/")
    public DriverRestDto getDriver(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        return driverService.getById(id, userDetails.getUsername());
    }
}

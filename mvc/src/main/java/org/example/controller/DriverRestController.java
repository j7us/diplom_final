package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.DriverRestDto;
import org.example.service.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<DriverRestDto> getDrivers(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam(required = false) UUID enterpriseId) {
        if (enterpriseId == null) {
            return driverService.getAll(userDetails.getUsername());
        }

        return driverService.getAllByEnterprise(userDetails.getUsername(), enterpriseId);
    }

    @GetMapping(value = "/drivers", params = {"page", "size"})
    public Page<DriverRestDto> getDriversPage(@AuthenticationPrincipal UserDetails userDetails,
                                              Pageable pageable,
                                              @RequestParam(required = false) UUID enterpriseId) {
        if (enterpriseId == null) {
            return driverService.getAll(userDetails.getUsername(), pageable);
        }

        return driverService.getAllByEnterprise(userDetails.getUsername(), enterpriseId, pageable);
    }

    @GetMapping("/drivers/{id}/")
    public DriverRestDto getDriver(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        return driverService.getById(id, userDetails.getUsername());
    }
}

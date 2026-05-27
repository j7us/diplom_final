package org.example.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.controller.dto.DriverRestDto;
import org.example.adapter.controller.mapper.DriverRestMapper;
import org.example.application.service.DriverService;
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

@Tag(name = "Водители", description = "Эндпоинты для данных о водителе")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class DriverRestController {
    private final DriverService driverService;
    private final DriverRestMapper driverRestMapper;

    @Operation(summary = "Получение всех водителей")
    @GetMapping("/drivers")
    public List<DriverRestDto> getDrivers(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam(required = false) UUID enterpriseId) {
        log.info("Пришел запрос /api/drivers с параметрами: {}, {}", userDetails.getUsername(), enterpriseId);

        if (enterpriseId == null) {
            return driverService.getAll(userDetails.getUsername()).stream()
                    .map(driverRestMapper::toDto)
                    .toList();
        }

        return driverService.getAllByEnterprise(userDetails.getUsername(), enterpriseId).stream()
                .map(driverRestMapper::toDto)
                .toList();
    }

    @Operation(summary = "Получение водителей по страницам")
    @GetMapping(value = "/drivers", params = {"page", "size"})
    public Page<DriverRestDto> getDriversPage(@AuthenticationPrincipal UserDetails userDetails,
                                              Pageable pageable,
                                              @RequestParam(required = false) UUID enterpriseId) {
        log.info("Пришел запрос /api/drivers с параметрами: {}, {}, {}", pageable.getPageNumber(), pageable.getPageSize(), enterpriseId);

        if (enterpriseId == null) {
            return driverService.getAll(userDetails.getUsername(), pageable).map(driverRestMapper::toDto);
        }

        return driverService.getAllByEnterprise(userDetails.getUsername(), enterpriseId, pageable).map(driverRestMapper::toDto);
    }

    @Operation(summary = "Получение водителя по id")
    @GetMapping("/drivers/{id}/")
    public DriverRestDto getDriver(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/drivers/{id}/ с параметрами: {}, {}", id, userDetails.getUsername());

        return driverRestMapper.toDto(driverService.getById(id, userDetails.getUsername()));
    }
}

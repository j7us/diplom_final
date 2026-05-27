package org.example.utils;

import org.example.adapter.repository.entity.*;
import org.example.adapter.controller.dto.AuthenticationResponseDto;
import org.example.adapter.controller.dto.LoginRequestDto;
import org.example.adapter.repository.jpa.EnterpriseRepository;
import org.example.adapter.repository.jpa.ManagerRepository;
import org.example.adapter.repository.jpa.VehicleRepository;
import org.example.adapter.repository.jpa.VehicleLocationRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestUtil {

    public static UUID saveEnterprise(EnterpriseRepository enterpriseRepository) {
        EnterpriseEntity enterpriseEntity = new EnterpriseEntity();

        enterpriseEntity.setId(UUID.randomUUID());
        enterpriseEntity.setName("Тестовое предприятие");
        enterpriseEntity.setCountry("Россия");
        enterpriseEntity.setProductionCapacity(1000);
        enterpriseEntity.setTimeZone("Europe/Moscow");

        enterpriseRepository.save(enterpriseEntity);

        return enterpriseEntity.getId();
    }

    public static UUID saveVehicle(VehicleRepository vehicleRepository,
                                   EnterpriseRepository enterpriseRepository,
                                   UUID enterpriseId) {
        EnterpriseEntity enterpriseEntity = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new IllegalArgumentException("Не найдено предприятие для создания машины"));

        VehicleEntity vehicleEntity = new VehicleEntity();

        vehicleEntity.setId(UUID.randomUUID());
        vehicleEntity.setMilleage(10000);
        vehicleEntity.setPrice(BigDecimal.valueOf(1500000));
        vehicleEntity.setCountry("Россия");
        vehicleEntity.setProductionDate(Instant.parse("2025-01-01T00:00:00Z"));
        vehicleEntity.setEnterpriseEntity(enterpriseEntity);

        vehicleRepository.save(vehicleEntity);

        return vehicleEntity.getId();
    }

    public static String buildAccessToken(TestRestTemplate testRestTemplate) {
        LoginRequestDto request = LoginRequestDto.builder().build();
        request.setUsername("manager1");
        request.setPassword("12345");

        // 2. Отправляем POST /login
        ResponseEntity<AuthenticationResponseDto> response =
                testRestTemplate.postForEntity("/login", request, AuthenticationResponseDto.class);



        return response.getBody().getAccessToken();
    }

    public static UUID saveManager(ManagerRepository managerRepository,
                                   EnterpriseRepository enterpriseRepository,
                                   UUID enterpriseId) {
        EnterpriseEntity enterpriseEntity = enterpriseRepository.findById(enterpriseId).orElseThrow();

        UUID managerId = UUID.randomUUID();
        ManagerEntity managerEntity = new ManagerEntity();
        AuthGrantedAuthority authGrantedAuthority = new AuthGrantedAuthority();

        managerEntity.setId(managerId);
        managerEntity.setUsername("manager1");
        managerEntity.setPassword("{bcrypt}$2a$10$mRbur.wbfxmFkq3X/vMO/eZfrOswp1MGI2fzRx63FNdwkXvBn.0ha");
        managerEntity.setAccountNonExpired(Boolean.TRUE);
        managerEntity.setAccountNonLocked(Boolean.TRUE);
        managerEntity.setCredentialsNonExpired(Boolean.TRUE);
        managerEntity.setEnabled(Boolean.TRUE);

        authGrantedAuthority.setId(UUID.randomUUID());
        authGrantedAuthority.setAuthority("MANAGER");
        authGrantedAuthority.setManagerEntity(managerEntity);
        managerEntity.setAuth(Collections.singletonList(authGrantedAuthority));

        managerRepository.save(managerEntity);

        List<ManagerEntity> managerEntities = new ArrayList<>();
        managerEntities.add(managerEntity);

        enterpriseEntity.setManagerEntities(managerEntities);
        enterpriseRepository.save(enterpriseEntity);

        return managerEntity.getId();
    }

    public static List<VehicleLocationEntity> initVehicleLocations(VehicleLocationRepository vehicleLocationRepository,
                                                                   VehicleRepository vehicleRepository,
                                                                   UUID vehicleId) {
        VehicleEntity vehicleEntity = vehicleRepository.findById(vehicleId).orElseThrow();
        List<VehicleLocationEntity> vehicleLocationEntities = new ArrayList<>();

        GeometryFactory geometryFactory = new GeometryFactory();
        LocalDateTime startDate = LocalDateTime.of(2025, 5, 1, 12, 0, 0);
        double first = 35.50;
        double second = 37.50;

        for (int i = 0; i < 5; i++) {
            VehicleLocationEntity vehicleLocationEntity = new VehicleLocationEntity();

            vehicleLocationEntity.setLocation(geometryFactory.createPoint(new Coordinate(first, second)));
            vehicleLocationEntity.setId(UUID.randomUUID());
            vehicleLocationEntity.setDate(startDate);
            vehicleLocationEntity.setVehicleEntity(vehicleEntity);

            vehicleLocationEntities.add(vehicleLocationEntity);

            startDate = startDate.plusDays(1L);
            first++;
            second++;
        }



        vehicleLocationRepository.saveAll(vehicleLocationEntities);

        return vehicleLocationEntities;
    }
}

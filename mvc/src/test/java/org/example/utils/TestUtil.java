package org.example.utils;

import org.example.dto.AuthenticationResponseDto;
import org.example.dto.LoginRequestDto;
import org.example.entity.Enterprise;
import org.example.entity.AuthGrantedAuthority;
import org.example.entity.Manager;
import org.example.entity.Vehicle;
import org.example.entity.VehicleLocation;
import org.example.repository.EnterpriseRepository;
import org.example.repository.ManagerRepository;
import org.example.repository.VehicleRepository;
import org.example.repository.VehicleLocationRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestUtil {

    public static UUID saveEnterprise(EnterpriseRepository enterpriseRepository) {
        Enterprise enterprise = new Enterprise();

        enterprise.setId(UUID.randomUUID());
        enterprise.setName("Тестовое предприятие");
        enterprise.setCountry("Россия");
        enterprise.setProductionCapacity(1000);
        enterprise.setTimeZone("Europe/Moscow");

        enterpriseRepository.save(enterprise);

        return enterprise.getId();
    }

    public static UUID saveVehicle(VehicleRepository vehicleRepository,
                                   EnterpriseRepository enterpriseRepository,
                                   UUID enterpriseId) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new IllegalArgumentException("Не найдено предприятие для создания машины"));

        Vehicle vehicle = new Vehicle();

        vehicle.setId(UUID.randomUUID());
        vehicle.setMilleage(10000);
        vehicle.setPrice(BigDecimal.valueOf(1500000));
        vehicle.setCountry("Россия");
        vehicle.setProductionDate(Instant.parse("2025-01-01T00:00:00Z"));
        vehicle.setEnterprise(enterprise);

        vehicleRepository.save(vehicle);

        return vehicle.getId();
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
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow();

        UUID managerId = UUID.randomUUID();
        Manager manager = new Manager();
        AuthGrantedAuthority authGrantedAuthority = new AuthGrantedAuthority();

        manager.setId(managerId);
        manager.setUsername("manager1");
        manager.setPassword("{bcrypt}$2a$10$mRbur.wbfxmFkq3X/vMO/eZfrOswp1MGI2fzRx63FNdwkXvBn.0ha");
        manager.setAccountNonExpired(Boolean.TRUE);
        manager.setAccountNonLocked(Boolean.TRUE);
        manager.setCredentialsNonExpired(Boolean.TRUE);
        manager.setEnabled(Boolean.TRUE);

        authGrantedAuthority.setId(UUID.randomUUID());
        authGrantedAuthority.setAuthority("MANAGER");
        authGrantedAuthority.setManager(manager);
        manager.setAuth(Collections.singletonList(authGrantedAuthority));

        managerRepository.save(manager);

        List<Manager> managers = new ArrayList<>();
        managers.add(manager);

        enterprise.setManagers(managers);
        enterpriseRepository.save(enterprise);

        return manager.getId();
    }

    public static List<VehicleLocation> initVehicleLocations(VehicleLocationRepository vehicleLocationRepository,
                                            VehicleRepository vehicleRepository,
                                            UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow();
        List<VehicleLocation> vehicleLocations = new ArrayList<>();

        GeometryFactory geometryFactory = new GeometryFactory();
        LocalDateTime startDate = LocalDateTime.of(2025, 5, 1, 12, 0, 0);
        double first = 35.50;
        double second = 37.50;

        for (int i = 0; i < 5; i++) {
            VehicleLocation vehicleLocation = new VehicleLocation();

            vehicleLocation.setLocation(geometryFactory.createPoint(new Coordinate(first, second)));
            vehicleLocation.setId(UUID.randomUUID());
            vehicleLocation.setDate(startDate);
            vehicleLocation.setVehicle(vehicle);

            vehicleLocations.add(vehicleLocation);

            startDate = startDate.plusDays(1L);
            first++;
            second++;
        }



        vehicleLocationRepository.saveAll(vehicleLocations);

        return vehicleLocations;
    }
}

package org.example.adapter.cli;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.example.adapter.controller.dto.DriverRestDto;
import org.example.adapter.controller.dto.brand.BrandRestDto;
import org.example.adapter.controller.dto.driver.DriverCreateDto;
import org.example.adapter.controller.dto.vehicle.VehicleCreateRestDto;
import org.example.adapter.controller.mapper.BrandRestMapper;
import org.example.adapter.controller.mapper.DriverRestMapper;
import org.example.adapter.controller.mapper.VehicleRestMapper;
import org.example.model.Driver;
import org.example.model.Vehicle;
import org.example.application.service.BrandService;
import org.example.application.service.DriverService;
import org.example.application.service.VehicleService;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

@Command
@Component
@RequiredArgsConstructor
public class EnterpriseVehicleGeneratorShell {
    private final VehicleService vehicleService;
    private final BrandService brandService;
    private final DriverService driverService;
    private final BrandRestMapper brandRestMapper;
    private final DriverRestMapper driverRestMapper;
    private final VehicleRestMapper vehicleRestMapper;

    @Command(description = "Create vehicles for enterprises")
    public String generateVehicles(@Option Integer numberOfVehicles, @Option UUID[] enterprises) {
        if (enterprises == null || enterprises.length == 0 || numberOfVehicles == null || numberOfVehicles <= 0) {
            throw new RuntimeException("Введены некорректные данные");
        }

        List<BrandRestDto> brands = brandService.getAll().stream()
                .map(brandRestMapper::toDto)
                .toList();

        int createdCount = 0;
        for (UUID enterpriseId : enterprises) {
            createdCount = createVehiclesWithDrivers(enterpriseId, brands, numberOfVehicles, createdCount);
        }

        return "success";
    }

    private int createVehiclesWithDrivers(UUID enterpriseId, List<BrandRestDto> brands, int numberOfVehicles, int createdCount) {
        for (int i = 0; i < numberOfVehicles; i++) {
            UUID driverId = null;

            if (needToCreateDriver(createdCount )) {
                DriverRestDto driver = createDriver(enterpriseId);
                driverId = driver.getId();
            }

            createVehicle(enterpriseId, brands, driverId);

            createdCount++;
        }

        return createdCount;
    }

    private void createVehicle(UUID enterpriseId, List<BrandRestDto> brands, UUID driverId) {
        VehicleCreateRestDto dto = new VehicleCreateRestDto();
        dto.setMilleage(randomMilleage());
        dto.setPrice(randomMoney(150000));
        dto.setCountry(randomCountry());
        dto.setBrandId(randomBrandId(brands));
        dto.setEnterpriseId(enterpriseId);
        dto.setActiveDriverId(driverId);
        dto.setProductionDate(OffsetDateTime.now());

        Vehicle vehicle = vehicleRestMapper.toModel(dto);

        vehicleService.createWithoutUsername(vehicle);
    }

    private DriverRestDto createDriver(UUID enterpriseId) {
        DriverCreateDto driverCreateDto = new DriverCreateDto();
        driverCreateDto.setName(randomDriverName());
        driverCreateDto.setSalary(randomMoney(120000));
        driverCreateDto.setWorkExperience(randomWorkExperience());
        driverCreateDto.setEnterpriseId(enterpriseId);

        Driver driver = driverRestMapper.toModel(driverCreateDto);

        return driverRestMapper.toDto(driverService.create(driver));
    }

    private UUID randomBrandId(List<BrandRestDto> brands) {
        if (brands.isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(brands.size());
        return brands.get(index).getId();
    }

    private Integer randomMilleage() {
        return ThreadLocalRandom.current().nextInt(0, 300000);
    }

    private BigDecimal randomMoney(double max) {
        double value = ThreadLocalRandom.current().nextDouble(max);
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal randomWorkExperience() {
        double value = ThreadLocalRandom.current().nextDouble(1, 40);
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private String randomCountry() {
        List<String> countr = List.of(
                "Германия",
                "Япония",
                "Россия"
        );

        int index = ThreadLocalRandom.current().nextInt(countr.size());
        return countr.get(index);
    }

    private String randomDriverName() {
        int index = ThreadLocalRandom.current().nextInt(1000);
        return "Driver " + index;
    }

    private boolean needToCreateDriver(int index) {
        return index % 10 == 0 && index > 0;
    }
}

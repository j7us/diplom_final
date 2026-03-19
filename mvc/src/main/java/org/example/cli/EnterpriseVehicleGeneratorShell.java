package org.example.cli;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.example.dto.DriverRestDto;
import org.example.dto.brand.BrandRestDto;
import org.example.dto.driver.DriverCreateDto;
import org.example.dto.vehicle.VehicleCreateRestDto;
import org.example.service.BrandService;
import org.example.service.DriverService;
import org.example.service.VehicleService;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

@Command
@Component
@RequiredArgsConstructor
public class EnterpriseVehicleGeneratorShell {
    private static final List<String> COUNTRIES = List.of(
            "Германия",
            "Япония",
            "Россия"
    );
    private static final Integer MAX_MONEY_FOR_DRIVER = 120000;
    private static final Integer MAX_DRIVER_YEAR_EXPERIENCE = 40;

    private final VehicleService vehicleService;
    private final BrandService brandService;
    private final DriverService driverService;

    @Command(description = "Create vehicles for enterprises")
    public String generateVehicles(@Option Integer numberOfVehicles, @Option UUID[] enterprises) {
        if (enterprises == null || enterprises.length == 0 || numberOfVehicles == null || numberOfVehicles <= 0) {
            throw new RuntimeException("Введены некорректные данные");
        }

        List<BrandRestDto> brands = brandService.getAllRest();

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

        vehicleService.createWithoutUsername(dto);
    }

    private DriverRestDto createDriver(UUID enterpriseId) {
        DriverCreateDto driverCreateDto = new DriverCreateDto();
        driverCreateDto.setName(randomDriverName());
        driverCreateDto.setSalary(randomMoney(MAX_MONEY_FOR_DRIVER));
        driverCreateDto.setWorkExperience(randomWorkExperience());
        driverCreateDto.setEnterpriseId(enterpriseId);

        return driverService.create(driverCreateDto);
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
        double value = ThreadLocalRandom.current().nextDouble(1, MAX_DRIVER_YEAR_EXPERIENCE);
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private String randomCountry() {
        int index = ThreadLocalRandom.current().nextInt(COUNTRIES.size());
        return COUNTRIES.get(index);
    }

    private String randomDriverName() {
        int index = ThreadLocalRandom.current().nextInt(1000);
        return "Driver " + index;
    }

    private boolean needToCreateDriver(int index) {
        return index % 10 == 0 && index > 0;
    }
}

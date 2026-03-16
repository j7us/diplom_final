package org.example.cli;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.example.dto.DriverRestDto;
import org.example.dto.brand.BrandRestDto;
import org.example.dto.driver.DriverCreateDto;
import org.example.dto.driver.DriverVehicleCreateDto;
import org.example.dto.vehicle.VehicleCreateRestDto;
import org.example.dto.vehicle.VehicleRestDto;
import org.example.service.BrandService;
import org.example.service.DriverService;
import org.example.service.DriverVehicleService;
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
    private final DriverVehicleService driverVehicleService;

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
            VehicleRestDto vehicle = createVehicle(enterpriseId, brands);

            createdCount++;

            if (needToCreateDriver(createdCount )) {
                DriverRestDto driver = createDriver(enterpriseId);
                linkDriverVehicle(vehicle.getId(), driver.getId());
            }
        }

        return createdCount;
    }

    private VehicleRestDto createVehicle(UUID enterpriseId, List<BrandRestDto> brands) {
        VehicleCreateRestDto dto = new VehicleCreateRestDto();
        dto.setMilleage(randomMilleage());
        dto.setPrice(randomMoney(150000));
        dto.setCountry(randomCountry());
        dto.setBrandId(randomBrandId(brands));
        dto.setEnterpriseId(enterpriseId);

        return vehicleService.create(dto);
    }

    private DriverRestDto createDriver(UUID enterpriseId) {
        DriverCreateDto driverCreateDto = new DriverCreateDto();
        driverCreateDto.setName(randomDriverName());
        driverCreateDto.setSalary(randomMoney(MAX_MONEY_FOR_DRIVER));
        driverCreateDto.setWorkExperience(randomWorkExperience());
        driverCreateDto.setEnterpriseId(enterpriseId);

        return driverService.create(driverCreateDto);
    }

    private void linkDriverVehicle(UUID vehicleId, UUID driverId) {
        DriverVehicleCreateDto driverVehicleCreateDto = new DriverVehicleCreateDto();
        driverVehicleCreateDto.setDriverId(driverId);
        driverVehicleCreateDto.setVehicleId(vehicleId);
        driverVehicleCreateDto.setActive(true);

        driverVehicleService.create(driverVehicleCreateDto);
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
        return index % 10 == 0;
    }
}

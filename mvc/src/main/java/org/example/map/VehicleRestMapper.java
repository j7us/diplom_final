package org.example.map;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.dto.vehicle.VehicleCreateRestDto;
import org.example.dto.vehicle.VehicleRestDto;
import org.example.dto.vehicle.VehicleUpdateRestDto;
import org.example.entity.Driver;
import org.example.entity.DriverVehicle;
import org.example.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.Context;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring")
public interface VehicleRestMapper {
    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(target = "activeDriverId", source = "driverVehicles", qualifiedByName = "findActiveDriver")
    @Mapping(target = "activeDriverName", source = "driverVehicles", qualifiedByName = "findActiveDriverName")
    @Mapping(target = "productionDate", source = ".", qualifiedByName = "mapInstantToOffsetDateTime")
    VehicleRestDto toDto(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "enterprise", ignore = true)
    @Mapping(target = "driverVehicles", ignore = true)
    Vehicle toEntity(VehicleCreateRestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "enterprise", ignore = true)
    @Mapping(target = "driverVehicles", ignore = true)
    void updateEntity(VehicleUpdateRestDto dto, @MappingTarget Vehicle vehicle);

    @Named("mapInstantToOffsetDateTime")
    default OffsetDateTime mapInstantToOffsetDateTime(Vehicle vehicle) {
        if (vehicle.getProductionDate() == null) {
            return null;
        }

        ZoneId zoneId = resolveZoneId(vehicle.getEnterprise().getTimeZone());
        return OffsetDateTime.ofInstant(vehicle.getProductionDate(), zoneId);
    }

    default Instant mapOffsetDateTimeToInstant(OffsetDateTime productionDate) {
        if (productionDate == null) {
            return null;
        }

        return productionDate.toInstant();
    }

    default ZoneId resolveZoneId(String timeZone) {
        if (!StringUtils.hasText(timeZone)) {
            return ZoneOffset.UTC;
        }

        try {
            return ZoneOffset.of(timeZone);
        } catch (DateTimeException ignored) {
        }

        try {
            return ZoneId.of(timeZone);
        } catch (DateTimeException ignored) {
            return ZoneOffset.UTC;
        }
    }

    @Named("findActiveDriver")
    default UUID findActiveDriver(List<DriverVehicle> activeDrivers) {
        if (CollectionUtils.isEmpty(activeDrivers)) {
            return null;
        }

        Optional<DriverVehicle> activeDriver = activeDrivers.stream()
                .filter(DriverVehicle::getActive)
                .findFirst();

        return activeDriver
                .map(DriverVehicle::getDriver)
                .map(Driver::getId)
                .orElse(null);
    }

    @Named("findActiveDriverName")
    default String findActiveDriverName(List<DriverVehicle> activeDrivers) {
        if (CollectionUtils.isEmpty(activeDrivers)) {
            return null;
        }

        Optional<DriverVehicle> activeDriver = activeDrivers.stream()
                .filter(DriverVehicle::getActive)
                .findFirst();

        return activeDriver
                .map(DriverVehicle::getDriver)
                .map(Driver::getName)
                .orElse(null);
    }
}

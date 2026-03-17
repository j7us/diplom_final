package org.example.map;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.dto.vehicle.VehicleCreateRestDto;
import org.example.dto.vehicle.VehicleRestDto;
import org.example.entity.Driver;
import org.example.entity.DriverVehicle;
import org.example.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = "spring")
public interface VehicleRestMapper {
    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(target = "activeDriverId", source = "driverVehicles", qualifiedByName = "findActiveDriver")
    @Mapping(target = "activeDriverName", source = "driverVehicles", qualifiedByName = "findActiveDriverName")
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
    void updateEntity(VehicleRestDto dto, @MappingTarget Vehicle vehicle);

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

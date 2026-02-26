package org.example.map;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.dto.VehicleRestDto;
import org.example.entity.Driver;
import org.example.entity.DriverVehicle;
import org.example.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VehicleRestMapper {
    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(target = "activeDriverId", source = "driverVehicles", qualifiedByName = "findActiveDriver")
    VehicleRestDto toDto(Vehicle vehicle);

    @Named("findActiveDriver")
    default UUID findActiveDriver(List<DriverVehicle> activeDrivers) {
        Optional<DriverVehicle> activeDriver = activeDrivers.stream()
                .filter(DriverVehicle::getActive)
                .findFirst();

        return activeDriver
                .map(DriverVehicle::getDriver)
                .map(Driver::getId)
                .orElse(null);
    }
}

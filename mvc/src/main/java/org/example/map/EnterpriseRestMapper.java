package org.example.map;

import java.util.List;
import java.util.UUID;
import org.example.dto.EnterpriseRestDto;
import org.example.entity.Driver;
import org.example.entity.Enterprise;
import org.example.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnterpriseRestMapper {
    @Mapping(source = "drivers", target = "driverIds")
    @Mapping(source = "vehicles", target = "vehicleIds")
    EnterpriseRestDto toDto(Enterprise enterprise);

    default List<UUID> mapDrivers(List<Driver> drivers) {
        if (drivers == null) {
            return List.of();
        }

        return drivers.stream()
                .map(Driver::getId)
                .toList();
    }

    default List<UUID> mapVehicles(List<Vehicle> vehicles) {
        if (vehicles == null) {
            return List.of();
        }

        return vehicles.stream()
                .map(Vehicle::getId)
                .toList();
    }
}

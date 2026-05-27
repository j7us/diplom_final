package org.example.adapter.repository.mapper;

import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.DriverEntity;
import org.example.adapter.repository.entity.DriverVehicleEntity;
import org.example.adapter.repository.entity.VehicleEntity;
import org.example.model.DriverVehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", implementationName = "AdapterDriverVehicleMapperImpl")
public interface DriverVehicleMapper {
    @Mapping(target = "driverId", source = "driverEntity.id")
    @Mapping(target = "vehicleId", source = "vehicleEntity.id")
    DriverVehicle toModel(DriverVehicleEntity driverVehicleEntity);

    List<DriverVehicle> toModel(List<DriverVehicleEntity> driverVehicleEntities);

    @Mapping(target = "driverEntity", source = "driverId", qualifiedByName = "toDriverEntity")
    @Mapping(target = "vehicleEntity", source = "vehicleId", qualifiedByName = "toVehicleEntity")
    DriverVehicleEntity toEntity(DriverVehicle driverVehicle);

    @Named("toDriverEntity")
    default DriverEntity toDriverEntity(UUID driverId) {
        if (driverId == null) {
            return null;
        }

        DriverEntity driverEntity = new DriverEntity();
        driverEntity.setId(driverId);

        return driverEntity;
    }

    @Named("toVehicleEntity")
    default VehicleEntity toVehicleEntity(UUID vehicleId) {
        if (vehicleId == null) {
            return null;
        }

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setId(vehicleId);

        return vehicleEntity;
    }
}

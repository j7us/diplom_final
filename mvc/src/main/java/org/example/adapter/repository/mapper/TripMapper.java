package org.example.adapter.repository.mapper;

import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.TripEntity;
import org.example.adapter.repository.entity.VehicleEntity;
import org.example.model.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", implementationName = "AdapterTripMapperImpl")
public interface TripMapper {
    @Mapping(target = "vehicleId", source = "vehicleEntity.id")
    Trip toModel(TripEntity tripEntity);

    List<Trip> toModel(List<TripEntity> tripEntities);

    @Mapping(target = "vehicleEntity", source = "vehicleId", qualifiedByName = "toVehicleEntity")
    TripEntity toEntity(Trip trip);

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

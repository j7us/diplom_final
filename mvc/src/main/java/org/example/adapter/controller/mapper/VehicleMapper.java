package org.example.adapter.controller.mapper;

import org.example.adapter.controller.dto.vehicle.VehicleDto;
import org.example.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    VehicleDto toDto(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productionDate", ignore = true)
    @Mapping(target = "enterpriseId", ignore = true)
    @Mapping(target = "enterpriseTimeZone", ignore = true)
    @Mapping(target = "activeDriverId", ignore = true)
    @Mapping(target = "activeDriverName", ignore = true)
    Vehicle toModel(VehicleDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productionDate", ignore = true)
    @Mapping(target = "enterpriseId", ignore = true)
    @Mapping(target = "enterpriseTimeZone", ignore = true)
    @Mapping(target = "activeDriverId", ignore = true)
    @Mapping(target = "activeDriverName", ignore = true)
    void updateModel(VehicleDto dto, @MappingTarget Vehicle vehicle);
}

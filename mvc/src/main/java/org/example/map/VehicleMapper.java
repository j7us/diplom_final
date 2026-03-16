package org.example.map;

import org.example.dto.vehicle.VehicleDto;
import org.example.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "brand.id", target = "brandId")
    VehicleDto toDto(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    Vehicle toEntity(VehicleDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    void updateEntity(VehicleDto dto, @MappingTarget Vehicle vehicle);
}

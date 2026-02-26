package org.example.map;

import org.example.dto.VehicleRestDto;
import org.example.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleRestMapper {
    @Mapping(source = "brand.id", target = "brandId")
    VehicleRestDto toDto(Vehicle vehicle);
}

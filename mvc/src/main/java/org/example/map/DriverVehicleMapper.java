package org.example.map;

import org.example.dto.driver.DriverVehicleCreateDto;
import org.example.entity.DriverVehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverVehicleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    DriverVehicle toEntity(DriverVehicleCreateDto dto);
}

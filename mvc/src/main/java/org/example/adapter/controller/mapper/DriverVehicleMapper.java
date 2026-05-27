package org.example.adapter.controller.mapper;

import org.example.adapter.controller.dto.driver.DriverVehicleCreateDto;
import org.example.model.DriverVehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverVehicleMapper {
    @Mapping(target = "id", ignore = true)
    DriverVehicle toModel(DriverVehicleCreateDto dto);
}

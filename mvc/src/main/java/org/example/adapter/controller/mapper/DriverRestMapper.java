package org.example.adapter.controller.mapper;

import org.example.adapter.controller.dto.DriverRestDto;
import org.example.adapter.controller.dto.driver.DriverCreateDto;
import org.example.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverRestMapper {
    DriverRestDto toDto(Driver driver);

    @Mapping(target = "id", ignore = true)
    Driver toModel(DriverCreateDto dto);
}

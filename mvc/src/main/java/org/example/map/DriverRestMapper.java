package org.example.map;

import org.example.dto.DriverRestDto;
import org.example.dto.driver.DriverCreateDto;
import org.example.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverRestMapper {
    DriverRestDto toDto(Driver driver);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enterprise", ignore = true)
    @Mapping(target = "driverVehicles", ignore = true)
    Driver toEntity(DriverCreateDto dto);
}

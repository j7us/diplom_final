package org.example.map;

import org.example.dto.DriverRestDto;
import org.example.entity.Driver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverRestMapper {
    DriverRestDto toDto(Driver driver);
}

package org.example.map;

import org.example.dto.BrandRestDto;
import org.example.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandRestMapper {
    BrandRestDto toDto(Brand brand);
}

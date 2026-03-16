package org.example.map;

import org.example.dto.brand.BrandDto;
import org.example.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDto toDto(Brand brand);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    Brand toEntity(BrandDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    void updateEntity(BrandDto dto, @MappingTarget Brand brand);
}

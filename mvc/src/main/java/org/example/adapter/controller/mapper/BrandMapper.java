package org.example.adapter.controller.mapper;

import org.example.adapter.controller.dto.brand.BrandDto;
import org.example.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDto toDto(Brand brand);

    @Mapping(target = "id", ignore = true)
    Brand toModel(BrandDto dto);

    @Mapping(target = "id", ignore = true)
    void updateModel(BrandDto dto, @MappingTarget Brand brand);
}

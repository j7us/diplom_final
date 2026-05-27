package org.example.adapter.controller.mapper;

import org.example.adapter.controller.dto.brand.BrandRestDto;
import org.example.model.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandRestMapper {
    BrandRestDto toDto(Brand brand);
}

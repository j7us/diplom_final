package org.example.adapter.repository.mapper;

import org.example.adapter.repository.entity.BrandEntity;
import org.example.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", implementationName = "AdapterBrandMapperImpl")
public interface BrandMapper {
    Brand toModel(BrandEntity brandEntity);

    @Mapping(target = "vehicleEntities", ignore = true)
    BrandEntity toEntity(Brand brand);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicleEntities", ignore = true)
    void updateEntity(Brand brand, @MappingTarget BrandEntity brandEntity);
}

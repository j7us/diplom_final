package org.example.adapter.batch.mapper;

import org.example.adapter.batch.dto.EnterpriseImport;
import org.example.adapter.repository.entity.EnterpriseEntity;
import org.example.model.Enterprise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnterpriseBatchMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driverIds", ignore = true)
    @Mapping(target = "vehicleIds", ignore = true)
    @Mapping(target = "managerIds", ignore = true)
    Enterprise toModel(EnterpriseImport source);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "productionCapacity", source = "productionCapacity")
    @Mapping(target = "timeZone", source = "timeZone")
    EnterpriseImport toImport(EnterpriseEntity source);
}

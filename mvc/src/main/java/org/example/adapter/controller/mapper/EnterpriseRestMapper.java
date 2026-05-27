package org.example.adapter.controller.mapper;

import java.util.List;
import org.example.adapter.controller.dto.EnterpriseRestDto;
import org.example.adapter.batch.dto.EnterpriseImport;
import org.example.model.Enterprise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EnterpriseRestMapper {
    EnterpriseRestDto toDto(Enterprise enterprise);

    List<EnterpriseRestDto> toDto(List<Enterprise> enterprises);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driverIds", ignore = true)
    @Mapping(target = "vehicleIds", ignore = true)
    EnterpriseRestDto toRestDto(EnterpriseImport dto);

    EnterpriseImport toImport(Enterprise enterprise);

    @Mapping(target = "managerIds", ignore = true)
    Enterprise toModel(EnterpriseRestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driverIds", ignore = true)
    @Mapping(target = "vehicleIds", ignore = true)
    @Mapping(target = "managerIds", ignore = true)
    Enterprise toModel(EnterpriseImport dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driverIds", ignore = true)
    @Mapping(target = "vehicleIds", ignore = true)
    @Mapping(target = "managerIds", ignore = true)
    void updateModel(EnterpriseRestDto dto, @MappingTarget Enterprise enterprise);
}

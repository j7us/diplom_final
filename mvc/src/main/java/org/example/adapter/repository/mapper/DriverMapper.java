package org.example.adapter.repository.mapper;

import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.DriverEntity;
import org.example.adapter.repository.entity.EnterpriseEntity;
import org.example.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DriverMapper {
    @Mapping(target = "enterpriseId", source = "enterpriseEntity.id")
    Driver toModel(DriverEntity driverEntity);

    List<Driver> toModel(List<DriverEntity> driverEntities);

    @Mapping(target = "enterpriseEntity", source = "enterpriseId", qualifiedByName = "toEnterpriseEntity")
    @Mapping(target = "driverVehicleEntities", ignore = true)
    DriverEntity toEntity(Driver driver);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enterpriseEntity", ignore = true)
    @Mapping(target = "driverVehicleEntities", ignore = true)
    void updateEntity(Driver driver, @MappingTarget DriverEntity driverEntity);

    @Named("toEnterpriseEntity")
    default EnterpriseEntity toEnterpriseEntity(UUID enterpriseId) {
        if (enterpriseId == null) {
            return null;
        }

        EnterpriseEntity enterpriseEntity = new EnterpriseEntity();
        enterpriseEntity.setId(enterpriseId);

        return enterpriseEntity;
    }
}

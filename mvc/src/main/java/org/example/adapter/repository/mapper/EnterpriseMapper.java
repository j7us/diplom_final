package org.example.adapter.repository.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.DriverEntity;
import org.example.adapter.repository.entity.EnterpriseEntity;
import org.example.adapter.repository.entity.ManagerEntity;
import org.example.adapter.repository.entity.VehicleEntity;
import org.example.model.Enterprise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = "spring")
public interface EnterpriseMapper {
    @Mapping(target = "driverIds", source = "driverEntities", qualifiedByName = "toDriverIds")
    @Mapping(target = "vehicleIds", source = "vehicleEntities", qualifiedByName = "toVehicleIds")
    @Mapping(target = "managerIds", source = "managerEntities", qualifiedByName = "toManagerIds")
    Enterprise toModel(EnterpriseEntity enterpriseEntity);

    List<Enterprise> toModel(List<EnterpriseEntity> enterpriseEntities);

    @Mapping(target = "driverEntities", ignore = true)
    @Mapping(target = "vehicleEntities", ignore = true)
    @Mapping(target = "managerEntities", source = "managerIds", qualifiedByName = "toManagerEntities")
    EnterpriseEntity toEntity(Enterprise enterprise);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "driverEntities", ignore = true)
    @Mapping(target = "vehicleEntities", ignore = true)
    @Mapping(target = "managerEntities", ignore = true)
    void updateEntity(Enterprise enterprise, @MappingTarget EnterpriseEntity enterpriseEntity);

    @Named("toDriverIds")
    default List<UUID> toDriverIds(List<DriverEntity> driverEntities) {
        if (CollectionUtils.isEmpty(driverEntities)) {
            return new ArrayList<>();
        }

        return driverEntities.stream()
                .map(DriverEntity::getId)
                .toList();
    }

    @Named("toVehicleIds")
    default List<UUID> toVehicleIds(List<VehicleEntity> vehicleEntities) {
        if (CollectionUtils.isEmpty(vehicleEntities)) {
            return new ArrayList<>();
        }

        return vehicleEntities.stream()
                .map(VehicleEntity::getId)
                .toList();
    }

    @Named("toManagerIds")
    default List<UUID> toManagerIds(List<ManagerEntity> managerEntities) {
        if (CollectionUtils.isEmpty(managerEntities)) {
            return new ArrayList<>();
        }

        return managerEntities.stream()
                .map(ManagerEntity::getId)
                .toList();
    }

    @Named("toManagerEntities")
    default List<ManagerEntity> toManagerEntities(List<UUID> managerIds) {
        if (CollectionUtils.isEmpty(managerIds)) {
            return new ArrayList<>();
        }

        return managerIds.stream()
                .map(this::toManagerEntity)
                .toList();
    }

    default ManagerEntity toManagerEntity(UUID id) {
        ManagerEntity managerEntity = new ManagerEntity();
        managerEntity.setId(id);

        return managerEntity;
    }
}

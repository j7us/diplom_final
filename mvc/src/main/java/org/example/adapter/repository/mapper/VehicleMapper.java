package org.example.adapter.repository.mapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.adapter.repository.entity.BrandEntity;
import org.example.adapter.repository.entity.DriverEntity;
import org.example.adapter.repository.entity.DriverVehicleEntity;
import org.example.adapter.repository.entity.EnterpriseEntity;
import org.example.adapter.repository.entity.VehicleEntity;
import org.example.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = "spring", implementationName = "AdapterVehicleMapperImpl")
public interface VehicleMapper {
    @Mapping(target = "brandId", source = "brandEntity.id")
    @Mapping(target = "brandName", source = "brandEntity.name")
    @Mapping(target = "enterpriseId", source = "enterpriseEntity.id")
    @Mapping(target = "enterpriseTimeZone", source = "enterpriseEntity.timeZone")
    @Mapping(target = "activeDriverId", source = "driverVehicleEntities", qualifiedByName = "findActiveDriverId")
    @Mapping(target = "activeDriverName", source = "driverVehicleEntities", qualifiedByName = "findActiveDriverName")
    Vehicle toModel(VehicleEntity vehicleEntity);

    List<Vehicle> toModel(List<VehicleEntity> vehicleEntities);

    @Mapping(target = "brandEntity", source = "brandId", qualifiedByName = "toBrandEntity")
    @Mapping(target = "enterpriseEntity", source = "enterpriseId", qualifiedByName = "toEnterpriseEntity")
    @Mapping(target = "driverVehicleEntities", ignore = true)
    VehicleEntity toEntity(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandEntity", ignore = true)
    @Mapping(target = "enterpriseEntity", ignore = true)
    @Mapping(target = "driverVehicleEntities", ignore = true)
    void updateEntity(Vehicle vehicle, @MappingTarget VehicleEntity vehicleEntity);

    @Named("findActiveDriverId")
    default UUID findActiveDriverId(List<DriverVehicleEntity> driverVehicleEntities) {
        return findActiveDriver(driverVehicleEntities)
                .map(DriverVehicleEntity::getDriverEntity)
                .map(DriverEntity::getId)
                .orElse(null);
    }

    @Named("findActiveDriverName")
    default String findActiveDriverName(List<DriverVehicleEntity> driverVehicleEntities) {
        return findActiveDriver(driverVehicleEntities)
                .map(DriverVehicleEntity::getDriverEntity)
                .map(DriverEntity::getName)
                .orElse(null);
    }

    default Optional<DriverVehicleEntity> findActiveDriver(List<DriverVehicleEntity> driverVehicleEntities) {
        if (CollectionUtils.isEmpty(driverVehicleEntities)) {
            return Optional.empty();
        }

        return driverVehicleEntities.stream()
                .filter(driverVehicle -> Boolean.TRUE.equals(driverVehicle.getActive()))
                .findFirst();
    }

    @Named("toBrandEntity")
    default BrandEntity toBrandEntity(UUID brandId) {
        if (brandId == null) {
            return null;
        }

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setId(brandId);

        return brandEntity;
    }

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

package org.example.adapter.controller.mapper;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.example.adapter.controller.dto.vehicle.VehicleCreateRestDto;
import org.example.adapter.controller.dto.vehicle.VehicleRestDto;
import org.example.adapter.controller.dto.vehicle.VehicleUpdateRestDto;
import org.example.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring")
public interface VehicleRestMapper {
    @Mapping(target = "productionDate", source = ".", qualifiedByName = "mapInstantToOffsetDateTime")
    VehicleRestDto toDto(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandName", ignore = true)
    @Mapping(target = "enterpriseTimeZone", ignore = true)
    @Mapping(target = "activeDriverName", ignore = true)
    Vehicle toModel(VehicleCreateRestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandName", ignore = true)
    @Mapping(target = "enterpriseId", ignore = true)
    @Mapping(target = "enterpriseTimeZone", ignore = true)
    @Mapping(target = "activeDriverName", ignore = true)
    Vehicle toModel(VehicleUpdateRestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandName", ignore = true)
    @Mapping(target = "enterpriseId", ignore = true)
    @Mapping(target = "enterpriseTimeZone", ignore = true)
    @Mapping(target = "activeDriverName", ignore = true)
    void updateModel(VehicleUpdateRestDto dto, @MappingTarget Vehicle vehicle);

    @Named("mapInstantToOffsetDateTime")
    default OffsetDateTime mapInstantToOffsetDateTime(Vehicle vehicle) {
        if (vehicle.getProductionDate() == null) {
            return null;
        }

        ZoneId zoneId = resolveZoneId(vehicle.getEnterpriseTimeZone());
        return OffsetDateTime.ofInstant(vehicle.getProductionDate(), zoneId);
    }

    default Instant mapOffsetDateTimeToInstant(OffsetDateTime productionDate) {
        if (productionDate == null) {
            return null;
        }

        return productionDate.toInstant();
    }

    default ZoneId resolveZoneId(String timeZone) {
        if (!StringUtils.hasText(timeZone)) {
            return ZoneOffset.UTC;
        }

        try {
            return ZoneOffset.of(timeZone);
        } catch (DateTimeException ignored) {
        }

        try {
            return ZoneId.of(timeZone);
        } catch (DateTimeException ignored) {
            return ZoneOffset.UTC;
        }
    }
}

package org.example.map;

import org.example.dto.vehiclelocation.VehicleLocationCreateRestDto;
import org.example.dto.vehiclelocation.VehicleLocationGeoJsonRestDto;
import org.example.dto.vehiclelocation.VehicleLocationJsonRestDto;
import org.example.entity.VehicleLocation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VehicleLocationMapper {
    @Mapping(target = "latitude", source = "location", qualifiedByName = "mapLatitude")
    @Mapping(target = "longitude", source = "location", qualifiedByName = "mapLongitude")
    VehicleLocationJsonRestDto toJsonDto(VehicleLocation location);

    VehicleLocationGeoJsonRestDto toGeoJsonDto(VehicleLocation location);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "location", source = ".", qualifiedByName = "createPoint")
    VehicleLocation toEntity(VehicleLocationCreateRestDto dto);

    @Named("mapLatitude")
    default Double mapLatitude(Point location) {
        if (location == null) {
            return null;
        }

        return location.getY();
    }

    @Named("mapLongitude")
    default Double mapLongitude(Point location) {
        if (location == null) {
            return null;
        }

        return location.getX();
    }

    @Named("createPoint")
    default Point createPoint(VehicleLocationCreateRestDto dto) {
        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            return null;
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
    }
}

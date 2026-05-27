package org.example.adapter.controller.mapper;

import org.example.adapter.controller.dto.vehiclelocation.VehicleLocationCreateRestDto;
import org.example.adapter.controller.dto.vehiclelocation.VehicleLocationGeoJsonRestDto;
import org.example.adapter.controller.dto.vehiclelocation.VehicleLocationJsonRestDto;
import org.example.adapter.controller.dto.trip.TripPointRestDto;
import org.example.model.GeoPoint;
import org.example.model.VehicleLocation;
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

    @Mapping(target = "location", source = "location", qualifiedByName = "createPoint")
    VehicleLocationGeoJsonRestDto toGeoJsonDto(VehicleLocation location);

    @Mapping(target = "latitude", source = "location", qualifiedByName = "mapLatitude")
    @Mapping(target = "longitude", source = "location", qualifiedByName = "mapLongitude")
    TripPointRestDto toTripPointDto(VehicleLocation location);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicleId", ignore = true)
    @Mapping(target = "location", source = ".", qualifiedByName = "createPoint")
    VehicleLocation toModel(VehicleLocationCreateRestDto dto);

    @Named("mapLatitude")
    default Double mapLatitude(GeoPoint location) {
        if (location == null) {
            return null;
        }

        return location.getLatitude();
    }

    @Named("mapLongitude")
    default Double mapLongitude(GeoPoint location) {
        if (location == null) {
            return null;
        }

        return location.getLongitude();
    }

    @Named("createPoint")
    default GeoPoint createPoint(VehicleLocationCreateRestDto dto) {
        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            return null;
        }

        return new GeoPoint(dto.getLatitude(), dto.getLongitude());
    }

    @Named("createPoint")
    default Point createPoint(GeoPoint point) {
        if (point == null) {
            return null;
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(point.getLongitude(), point.getLatitude()));
    }
}

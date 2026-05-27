package org.example.adapter.repository.mapper;

import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.VehicleEntity;
import org.example.adapter.repository.entity.VehicleLocationEntity;
import org.example.model.GeoPoint;
import org.example.model.VehicleLocation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", implementationName = "AdapterVehicleLocationMapperImpl")
public interface VehicleLocationMapper {
    @Mapping(target = "vehicleId", source = "vehicleEntity.id")
    @Mapping(target = "location", source = "location", qualifiedByName = "toGeoPoint")
    VehicleLocation toModel(VehicleLocationEntity vehicleLocationEntity);

    List<VehicleLocation> toModel(List<VehicleLocationEntity> vehicleLocationEntities);

    @Mapping(target = "vehicleEntity", source = "vehicleId", qualifiedByName = "toVehicleEntity")
    @Mapping(target = "location", source = "location", qualifiedByName = "toPoint")
    VehicleLocationEntity toEntity(VehicleLocation vehicleLocation);

    List<VehicleLocationEntity> toEntity(List<VehicleLocation> vehicleLocations);

    @Named("toGeoPoint")
    default GeoPoint toGeoPoint(Point point) {
        if (point == null) {
            return null;
        }

        return new GeoPoint(point.getY(), point.getX());
    }

    @Named("toPoint")
    default Point toPoint(GeoPoint geoPoint) {
        if (geoPoint == null) {
            return null;
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(geoPoint.getLongitude(), geoPoint.getLatitude()));
    }

    @Named("toVehicleEntity")
    default VehicleEntity toVehicleEntity(UUID vehicleId) {
        if (vehicleId == null) {
            return null;
        }

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setId(vehicleId);

        return vehicleEntity;
    }
}

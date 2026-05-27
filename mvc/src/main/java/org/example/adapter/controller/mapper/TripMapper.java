package org.example.adapter.controller.mapper;

import org.example.adapter.controller.dto.trip.TripRestDto;
import org.example.application.dto.trip.TripDetails;
import org.example.model.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = VehicleLocationMapper.class)
public interface TripMapper {
    @Mapping(source = "dateFrom", target = "startDate")
    @Mapping(source = "dateTo", target = "endDate")
    @Mapping(target = "startPoint", ignore = true)
    @Mapping(target = "startAddress", ignore = true)
    @Mapping(target = "endPoint", ignore = true)
    @Mapping(target = "endAddress", ignore = true)
    TripRestDto toDto(Trip trip);

    @Mapping(source = "trip.id", target = "id")
    @Mapping(source = "trip.dateFrom", target = "startDate")
    @Mapping(source = "trip.dateTo", target = "endDate")
    @Mapping(source = "startLocation", target = "startPoint")
    @Mapping(source = "endLocation", target = "endPoint")
    TripRestDto toDto(TripDetails tripDetails);
}

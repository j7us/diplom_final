package org.example.map;

import org.example.dto.trip.TripRestDto;
import org.example.entity.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {
    @Mapping(source = "dateFrom", target = "startDate")
    @Mapping(source = "dateTo", target = "endDate")
    @Mapping(target = "startPoint", ignore = true)
    @Mapping(target = "startAddress", ignore = true)
    @Mapping(target = "endPoint", ignore = true)
    @Mapping(target = "endAddress", ignore = true)
    TripRestDto toDto(Trip trip);
}

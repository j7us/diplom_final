package org.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.example.dto.vehiclelocation.VehicleLocationCreateRestDto;
import org.example.dto.vehiclelocation.VehicleLocationGeoJsonRestDto;
import org.example.dto.vehiclelocation.VehicleLocationJsonRestDto;
import org.example.entity.Vehicle;
import org.example.entity.VehicleLocation;
import org.example.map.VehicleLocationMapper;
import org.example.repository.VehicleLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleLocationService {
    private final VehicleLocationRepository vehicleLocationRepository;
    private final VehicleLocationMapper vehicleLocationMapper;
    private final VehicleService vehicleService;

    @Transactional
    public VehicleLocationJsonRestDto create(UUID vehicleId,
                                             VehicleLocationCreateRestDto dto,
                                             String username) {
        Vehicle vehicle = vehicleService.getEntityByIdAndManagerUsername(vehicleId, username);

        VehicleLocation location = vehicleLocationMapper.toEntity(dto);
        location.setId(UUID.randomUUID());
        location.setVehicle(vehicle);

        VehicleLocation savedLocation = vehicleLocationRepository.save(location);

        return vehicleLocationMapper.toJsonDto(savedLocation);
    }

    @Transactional
    public VehicleLocationJsonRestDto create(UUID vehicleId,
                                             VehicleLocationCreateRestDto dto) {
        Vehicle vehicle = vehicleService.getEntityById(vehicleId);

        VehicleLocation location = vehicleLocationMapper.toEntity(dto);
        location.setId(UUID.randomUUID());
        location.setVehicle(vehicle);

        VehicleLocation savedLocation = vehicleLocationRepository.save(location);

        return vehicleLocationMapper.toJsonDto(savedLocation);
    }


    public List<VehicleLocationJsonRestDto> getAllJson(UUID vehicleId,
                                                       LocalDateTime dateFrom,
                                                       LocalDateTime dateTo,
                                                       String username) {
        return getAllPointsAndMap(vehicleLocationMapper::toJsonDto, vehicleId, dateFrom, dateTo, username);
    }

    public List<VehicleLocationGeoJsonRestDto> getAllGeoJson(UUID vehicleId,
                                                             LocalDateTime dateFrom,
                                                             LocalDateTime dateTo,
                                                             String username) {
        return getAllPointsAndMap(vehicleLocationMapper::toGeoJsonDto, vehicleId, dateFrom, dateTo, username);
    }

    private <T> List<T> getAllPointsAndMap(Function<VehicleLocation, T> locationPointMapper,
                                           UUID vehicleId,
                                           LocalDateTime dateFrom,
                                           LocalDateTime dateTo,
                                           String username) {
        Vehicle vehicle = vehicleService.getEntityByIdAndManagerUsername(vehicleId, username);

        return vehicleLocationRepository.findAllByVehicle_IdAndDateBetween(vehicle.getId(), dateFrom, dateTo)
                .stream()
                .map(locationPointMapper)
                .toList();
    }
}

package org.example.adapter.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.adapter.repository.entity.VehicleLocationEntity;
import org.example.adapter.repository.mapper.VehicleLocationMapper;
import org.example.adapter.repository.specification.VehicleLocationSpecification;
import org.example.model.Trip;
import org.example.model.VehicleLocation;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleLocationRepositoryAdapter implements org.example.application.repository.VehicleLocationRepository {
    private final org.example.adapter.repository.jpa.VehicleLocationRepository vehicleLocationRepository;
    private final VehicleLocationMapper vehicleLocationMapper;

    @Override
    public VehicleLocation save(VehicleLocation location) {
        VehicleLocationEntity savedLocation = vehicleLocationRepository.save(vehicleLocationMapper.toEntity(location));

        return vehicleLocationMapper.toModel(savedLocation);
    }

    @Override
    public List<VehicleLocation> saveAll(List<VehicleLocation> locations) {
        return vehicleLocationMapper.toModel(vehicleLocationRepository.saveAll(vehicleLocationMapper.toEntity(locations)));
    }

    @Override
    public List<VehicleLocation> findAllByVehicleIdAndDateBetween(UUID vehicleId,
                                                                  LocalDateTime dateFrom,
                                                                  LocalDateTime dateTo) {
        return vehicleLocationMapper.toModel(
                vehicleLocationRepository.findAllByVehicleEntity_IdAndDateBetween(vehicleId, dateFrom, dateTo));
    }

    @Override
    public List<VehicleLocation> findAllByVehicleIdAndTrips(UUID vehicleId, List<Trip> trips) {
        return vehicleLocationMapper.toModel(
                vehicleLocationRepository.findAll(VehicleLocationSpecification.withinAnyTripDateRange(vehicleId, trips)));
    }

    @Override
    public Optional<VehicleLocation> findByVehicleIdAndDate(UUID vehicleId, Instant date) {
        LocalDateTime mappedDate = LocalDateTime.ofInstant(date, ZoneOffset.UTC);

        return vehicleLocationRepository.findByVehicleEntity_IdAndDate(vehicleId, mappedDate)
                .map(vehicleLocationMapper::toModel);
    }
}

package org.example.adapter.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.adapter.repository.mapper.TripMapper;
import org.example.model.Trip;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripRepositoryAdapter implements org.example.application.repository.TripRepository {
    private final org.example.adapter.repository.jpa.TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Override
    public Trip save(Trip trip) {
        return tripMapper.toModel(tripRepository.save(tripMapper.toEntity(trip)));
    }

    @Override
    public List<Trip> findAllByVehicleIdAndDateFromGreaterThanEqualAndDateToLessThanEqual(UUID vehicleId,
                                                                                          Instant dateFrom,
                                                                                          Instant dateTo) {
        return tripMapper.toModel(
                tripRepository.findAllByVehicleEntity_IdAndDateFromGreaterThanEqualAndDateToLessThanEqual(
                        vehicleId,
                        dateFrom,
                        dateTo));
    }

    @Override
    public Boolean existsByVehicleIdAndDateFromLessThanEqualAndDateToGreaterThanEqual(UUID vehicleId,
                                                                                      Instant dateTo,
                                                                                      Instant dateFrom) {
        return tripRepository.existsByVehicleEntity_IdAndDateFromLessThanEqualAndDateToGreaterThanEqual(
                vehicleId,
                dateTo,
                dateFrom);
    }
}

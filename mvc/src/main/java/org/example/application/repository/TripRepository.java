package org.example.application.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.example.model.Trip;

public interface TripRepository {
    Trip save(Trip trip);

    List<Trip> findAllByVehicleIdAndDateFromGreaterThanEqualAndDateToLessThanEqual(UUID vehicleId,
                                                                                   Instant dateFrom,
                                                                                   Instant dateTo);

    Boolean existsByVehicleIdAndDateFromLessThanEqualAndDateToGreaterThanEqual(UUID vehicleId,
                                                                               Instant dateTo,
                                                                               Instant dateFrom);
}

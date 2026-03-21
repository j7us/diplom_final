package org.example.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.example.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findAllByVehicle_IdAndDateFromGreaterThanEqualAndDateToLessThanEqual(UUID vehicleId,
                                                                                      Instant dateFrom,
                                                                                      Instant dateTo);
}

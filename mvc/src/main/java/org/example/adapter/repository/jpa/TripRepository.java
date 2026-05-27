package org.example.adapter.repository.jpa;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<TripEntity, UUID> {
    List<TripEntity> findAllByVehicleEntity_IdAndDateFromGreaterThanEqualAndDateToLessThanEqual(UUID vehicleId,
                                                                                                Instant dateFrom,
                                                                                                Instant dateTo);

    boolean existsByVehicleEntity_IdAndDateFromLessThanEqualAndDateToGreaterThanEqual(UUID vehicleId,
                                                                                      Instant dateTo,
                                                                                      Instant dateFrom);
}

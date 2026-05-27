package org.example.adapter.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.example.adapter.repository.entity.VehicleEntity;
import org.example.adapter.repository.entity.VehicleLocationEntity;
import org.example.model.Trip;
import org.springframework.data.jpa.domain.Specification;

public final class VehicleLocationSpecification {
    private VehicleLocationSpecification() {
    }

    public static Specification<VehicleLocationEntity> withinAnyTripDateRange(UUID vehicleId, List<Trip> trips) {
        return (root, query, criteriaBuilder) -> {
            Join<VehicleLocationEntity, VehicleEntity> vehicleJoin = root.join("vehicleEntity");
            Predicate vehiclePredicate = criteriaBuilder.equal(vehicleJoin.get("id"), vehicleId);

            List<Predicate> datePredicates = trips.stream()
                    .map(trip -> criteriaBuilder.between(root.get("date"),
                            mapToLocalDateTime(trip.getDateFrom()),
                            mapToLocalDateTime(trip.getDateTo())))
                    .toList();

            Predicate dateRangePredicate = criteriaBuilder.or(datePredicates.toArray(new Predicate[0]));

            return criteriaBuilder.and(vehiclePredicate, dateRangePredicate);
        };
    }

    private static LocalDateTime mapToLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}

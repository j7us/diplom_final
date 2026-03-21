package org.example.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.example.entity.Trip;
import org.example.entity.Vehicle;
import org.example.entity.VehicleLocation;
import org.springframework.data.jpa.domain.Specification;

public final class VehicleLocationSpecification {
    private VehicleLocationSpecification() {
    }

    public static Specification<VehicleLocation> withinAnyTripDateRange(UUID vehicleId, List<Trip> trips) {
        return (root, query, criteriaBuilder) -> {
            Join<VehicleLocation, Vehicle> vehicleJoin = root.join("vehicle");
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

package org.example.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.application.client.AddressClient;
import org.example.application.dto.trip.TripDetails;
import org.example.model.Trip;
import org.example.model.Vehicle;
import org.example.model.VehicleLocation;
import org.example.application.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {
    private final TripRepository tripRepository;
    private final VehicleService vehicleService;
    private final VehicleLocationService vehicleLocationService;
    private final AddressClient addressClient;

    @Transactional
    public void createTripWithLocations(UUID vehicleId, String username, List<VehicleLocation> locations) {
        Vehicle vehicle = vehicleService.getById(vehicleId, username);

        VehicleLocation startLocation = locations.getFirst();
        VehicleLocation endLocation = locations.getLast();
        Instant dateFrom = startLocation.getDate().toInstant(ZoneOffset.UTC);
        Instant dateTo = endLocation.getDate().toInstant(ZoneOffset.UTC);

        validateTripOverlap(vehicle.getId(), dateFrom, dateTo);

        locations.forEach(location -> location.setVehicleId(vehicle.getId()));

        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .vehicleId(vehicle.getId())
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .distance(BigDecimal.ZERO)
                .build();

        tripRepository.save(trip);

        vehicleLocationService.createAll(locations);
    }

    public List<VehicleLocation> getVehicleLocations(UUID vehicleId,
                                                     Instant dateFrom,
                                                     Instant dateTo,
                                                     String username) {
        List<Trip> trips = findTripsForVehicle(vehicleId, dateFrom, dateTo, username);

        if (CollectionUtils.isEmpty(trips)) {
            return List.of();
        }

        return vehicleLocationService.getAllByTrips(vehicleId, trips);
    }

    public List<TripDetails> getTrips(UUID vehicleId,
                                      Instant dateFrom,
                                      Instant dateTo,
                                      String username) {
        List<Trip> trips = findTripsForVehicle(vehicleId, dateFrom, dateTo, username);

        if (CollectionUtils.isEmpty(trips)) {
            return List.of();
        }

        return trips.stream()
                .map(trip -> buildTripDetails(vehicleId, trip))
                .toList();
    }

    public List<Trip> getTripModels(UUID vehicleId,
                                    Instant dateFrom,
                                    Instant dateTo,
                                    String username) {
        return findTripsForVehicle(vehicleId, dateFrom, dateTo, username);
    }

    private List<Trip> findTripsForVehicle(UUID vehicleId,
                                           Instant dateFrom,
                                           Instant dateTo,
                                           String username) {
        Vehicle vehicle = vehicleService.getById(vehicleId, username);

        return tripRepository.findAllByVehicleIdAndDateFromGreaterThanEqualAndDateToLessThanEqual(
                vehicle.getId(),
                dateFrom,
                dateTo);
    }

    private TripDetails buildTripDetails(UUID vehicleId, Trip trip) {
        VehicleLocation startLocation = vehicleLocationService.getByVehicleIdAndDate(vehicleId, trip.getDateFrom());
        VehicleLocation endLocation = vehicleLocationService.getByVehicleIdAndDate(vehicleId, trip.getDateTo());

        List<String> addresses = addressClient.getAddresses(List.of(startLocation, endLocation));

        return new TripDetails(
                trip,
                startLocation,
                endLocation,
                getAddress(addresses, 0),
                getAddress(addresses, 1));
    }

    private String getAddress(List<String> addresses, int index) {
        if (CollectionUtils.isEmpty(addresses) || addresses.size() <= index) {
            throw new RuntimeException("Geoapify не вернул адрес точки поездки");
        }

        return addresses.get(index);
    }

    private void validateTripOverlap(UUID vehicleId, Instant dateFrom, Instant dateTo) {
        if (tripRepository.existsByVehicleIdAndDateFromLessThanEqualAndDateToGreaterThanEqual(
                vehicleId,
                dateTo,
                dateFrom)) {
            throw new RuntimeException("Найдена поездка, пересекающаяся с интервалом GPX файла");
        }
    }
}

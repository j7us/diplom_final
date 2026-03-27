package org.example.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.client.GeoapifyAddressClient;
import org.example.dto.trip.TripRestDto;
import org.example.dto.vehiclelocation.VehicleLocationGeoJsonRestDto;
import org.example.entity.Trip;
import org.example.entity.Vehicle;
import org.example.entity.VehicleLocation;
import org.example.map.TripMapper;
import org.example.map.VehicleLocationMapper;
import org.example.repository.TripRepository;
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
    private final GeoapifyAddressClient geoapifyAddressClient;
    private final TripMapper tripMapper;
    private final VehicleLocationMapper vehicleLocationMapper;

    @Transactional
    public void createTripWithLocations(UUID vehicleId, String username, List<VehicleLocation> locations) {
        Vehicle vehicle = vehicleService.getEntityByIdAndManagerUsername(vehicleId, username);

        VehicleLocation startLocation = locations.getFirst();
        VehicleLocation endLocation = locations.getLast();
        Instant dateFrom = startLocation.getDate().toInstant(ZoneOffset.UTC);
        Instant dateTo = endLocation.getDate().toInstant(ZoneOffset.UTC);

        validateTripOverlap(vehicle.getId(), dateFrom, dateTo);

        locations.forEach(l -> l.setVehicle(vehicle));

        Trip trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setVehicle(vehicle);
        trip.setDateFrom(dateFrom);
        trip.setDateTo(dateTo);
        trip.setDistance(BigDecimal.ZERO);

        tripRepository.save(trip);

        vehicleLocationService.createAll(locations);
    }

    public List<VehicleLocationGeoJsonRestDto> getVehicleLocations(UUID vehicleId,
                                                                   Instant dateFrom,
                                                                   Instant dateTo,
                                                                   String username) {
        List<Trip> trips = findTripsForVehicle(vehicleId, dateFrom, dateTo, username);

        if (CollectionUtils.isEmpty(trips)) {
            return List.of();
        }

        return vehicleLocationService.getAllGeoJsonByTrips(vehicleId, trips);
    }

    public List<TripRestDto> getTrips(UUID vehicleId,
                                      Instant dateFrom,
                                      Instant dateTo,
                                      String username) {
        List<Trip> trips = findTripsForVehicle(vehicleId, dateFrom, dateTo, username);

        if (CollectionUtils.isEmpty(trips)) {
            return List.of();
        }

        return trips.stream()
                .map(trip -> mapToTripDto(vehicleId, trip))
                .toList();
    }

    public List<Trip> getTripEntities(UUID vehicleId,
                                      Instant dateFrom,
                                      Instant dateTo,
                                      String username) {
        return findTripsForVehicle(vehicleId, dateFrom, dateTo, username);
    }

    private List<Trip> findTripsForVehicle(UUID vehicleId,
                                           Instant dateFrom,
                                           Instant dateTo,
                                           String username) {
        Vehicle vehicle = vehicleService.getEntityByIdAndManagerUsername(vehicleId, username);

        return tripRepository.findAllByVehicle_IdAndDateFromGreaterThanEqualAndDateToLessThanEqual(
                vehicle.getId(),
                dateFrom,
                dateTo);
    }

    private TripRestDto mapToTripDto(UUID vehicleId, Trip trip) {
        VehicleLocation startLocation = vehicleLocationService.getEntityByVehicleIdAndDate(vehicleId, trip.getDateFrom());
        VehicleLocation endLocation = vehicleLocationService.getEntityByVehicleIdAndDate(vehicleId, trip.getDateTo());

        List<String> addresses = geoapifyAddressClient.getAddresses(List.of(startLocation, endLocation));

        TripRestDto dto = tripMapper.toDto(trip);
        dto.setStartPoint(vehicleLocationMapper.toTripPointDto(startLocation));
        dto.setEndPoint(vehicleLocationMapper.toTripPointDto(endLocation));
        dto.setStartAddress(getAddress(addresses, 0));
        dto.setEndAddress(getAddress(addresses, 1));

        return dto;
    }

    private String getAddress(List<String> addresses, int index) {
        if (CollectionUtils.isEmpty(addresses) || addresses.size() <= index) {
            throw new RuntimeException("Geoapify не вернул адрес точки поездки");
        }

        return addresses.get(index);
    }

    private void validateTripOverlap(UUID vehicleId, Instant dateFrom, Instant dateTo) {
        if (tripRepository.existsByVehicle_IdAndDateFromLessThanEqualAndDateToGreaterThanEqual(
                vehicleId,
                dateTo,
                dateFrom)) {
            throw new RuntimeException("Найдена поездка, пересекающаяся с интервалом GPX файла");
        }
    }
}

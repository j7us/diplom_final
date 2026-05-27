package org.example.adapter.file;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import lombok.RequiredArgsConstructor;
import org.example.application.service.TripService;
import org.example.model.GeoPoint;
import org.example.model.VehicleLocation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GpxParserService {
    private final TripService tripService;

    public void parseLocations(UUID vehicleId, MultipartFile file, String username) {
        GPX gpx = readGpx(file);

        List<VehicleLocation> locations = gpx.getTracks().stream()
                .flatMap(track -> track.getSegments().stream())
                .flatMap(segment -> segment.getPoints().stream())
                .map(this::mapToVehicleLocation)
                .sorted(Comparator.comparing(VehicleLocation::getDate))
                .toList();

        tripService.createTripWithLocations(vehicleId, username, locations);
    }

    private GPX readGpx(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return GPX.Reader.of(GPX.Reader.Mode.LENIENT).read(inputStream);
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось распарсить GPX файл", ex);
        }
    }

    private VehicleLocation mapToVehicleLocation(WayPoint wayPoint) {
        LocalDateTime pointDate = wayPoint.getTime()
                .map(instant -> LocalDateTime.ofInstant(instant, ZoneOffset.UTC))
                .orElse(LocalDateTime.now(ZoneOffset.UTC));

        VehicleLocation location = new VehicleLocation();
        location.setId(UUID.randomUUID());
        location.setLocation(new GeoPoint(
                wayPoint.getLatitude().doubleValue(),
                wayPoint.getLongitude().doubleValue()));
        location.setDate(pointDate);

        return location;
    }
}

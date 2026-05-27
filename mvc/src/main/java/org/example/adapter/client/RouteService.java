package org.example.adapter.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.adapter.client.graphhopper.dto.GraphHopperPointDto;
import org.example.adapter.client.graphhopper.dto.GraphHopperRouteResponseDto;
import org.example.model.GeoPoint;
import org.example.model.VehicleLocation;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final GraphHopperRouteClient graphHopperRouteClient;
    private final ObjectMapper objectMapper;

    public Flux<VehicleLocation> buildRoutePoints(GraphHopperPointDto pointFrom,
                                                  GraphHopperPointDto pointTo) {
        return graphHopperRouteClient.getRoute(pointFrom, pointTo)
                .map(this::mapToRouteResponse)
                .flatMapMany(this::mapToVehicleLocations);
    }

    private GraphHopperRouteResponseDto mapToRouteResponse(String response) {
        try {
            return objectMapper.readValue(response, GraphHopperRouteResponseDto.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Не удалось разобрать ответ GraphHopper", ex);
        }
    }

    private Flux<VehicleLocation> mapToVehicleLocations(GraphHopperRouteResponseDto routeResponse) {
        GraphHopperRouteResponseDto.GraphHopperRoutePathDto firstPath = routeResponse.getPaths().getFirst();

        if (firstPath.getPoints() == null || CollectionUtils.isEmpty(firstPath.getPoints().getCoordinates())) {
            throw new RuntimeException("GraphHopper не вернул координаты маршрута");
        }

        return Flux.fromIterable(firstPath.getPoints().getCoordinates())
                .map(this::mapToCreate);
    }

    private VehicleLocation mapToCreate(List<Double> coordinate) {
        VehicleLocation location = new VehicleLocation();

        location.setDate(LocalDateTime.now());
        location.setLocation(new GeoPoint(coordinate.get(1), coordinate.get(0)));

        return location;
    }
}

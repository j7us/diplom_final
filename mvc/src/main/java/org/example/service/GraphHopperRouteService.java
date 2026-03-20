package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.graphhopper.GraphHopperPointDto;
import org.example.dto.graphhopper.GraphHopperRouteResponseDto;
import org.example.dto.vehiclelocation.VehicleLocationCreateRestDto;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class GraphHopperRouteService {
    private final GraphHopperRouteClientService graphHopperRouteClientService;
    private final ObjectMapper objectMapper;

    public List<VehicleLocationCreateRestDto> buildRoutePoints(GraphHopperPointDto pointFrom,
                                                               GraphHopperPointDto pointTo) {
        String response = graphHopperRouteClientService.getRoute(pointFrom, pointTo);
        GraphHopperRouteResponseDto routeResponse;

        try {
            routeResponse = objectMapper.readValue(response, GraphHopperRouteResponseDto.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Не удалось разобрать ответ GraphHopper", ex);
        }

        return mapToVehicleLocations(routeResponse);
    }

    private List<VehicleLocationCreateRestDto> mapToVehicleLocations(GraphHopperRouteResponseDto routeResponse) {
        GraphHopperRouteResponseDto.GraphHopperRoutePathDto firstPath = routeResponse.getPaths().getFirst();

        if (firstPath.getPoints() == null || CollectionUtils.isEmpty(firstPath.getPoints().getCoordinates())) {
            throw new RuntimeException("GraphHopper не вернул координаты маршрута");
        }

        return firstPath.getPoints().getCoordinates().stream()
                .map(this::mapToCreate)
                .toList();
    }

    private VehicleLocationCreateRestDto mapToCreate(List<Double> coordinate) {
        VehicleLocationCreateRestDto dto = new VehicleLocationCreateRestDto();

        dto.setDate(LocalDateTime.now());
        dto.setLatitude(coordinate.get(1));
        dto.setLongitude(coordinate.get(0));

        return dto;
    }
}

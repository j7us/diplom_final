package org.example.dto.graphhopper;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraphHopperRouteResponseDto {
    private List<GraphHopperRoutePathDto> paths;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphHopperRoutePathDto {
        private GraphHopperRoutePointsDto points;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphHopperRoutePointsDto {
        private List<List<Double>> coordinates;
    }
}

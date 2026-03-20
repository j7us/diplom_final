package org.example.cli;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.example.config.GraphHopperProp;
import org.example.dto.graphhopper.GraphHopperPointDto;
import org.example.dto.vehiclelocation.VehicleLocationCreateRestDto;
import org.example.service.GraphHopperRouteService;
import org.example.service.VehicleLocationService;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

@Command
@Component
@RequiredArgsConstructor
public class VehicleLocationGeneratorShell {
    private final GraphHopperProp graphHopperProp;
    private final GraphHopperRouteService graphHopperRouteService;
    private final VehicleLocationService vehicleLocationService;

    @Command(description = "Generate route points for vehicle")
    public void generateVehicleLocations(@Option UUID vehicleId) {
        if (vehicleId == null) {
            throw new RuntimeException("Идентификатор машины не задан");
        }

        GraphHopperPointDto pointFrom = randomPoint();
        GraphHopperPointDto pointTo = randomPoint();

        List<VehicleLocationCreateRestDto> locations = graphHopperRouteService.buildRoutePoints(pointFrom, pointTo);

        createWithTime(vehicleId, locations);
    }

    private void createWithTime(UUID vehicleId, List<VehicleLocationCreateRestDto> locations) {
        for (VehicleLocationCreateRestDto location : locations) {
            vehicleLocationService.create(vehicleId, location);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private GraphHopperPointDto randomPoint() {
        double latitude = ThreadLocalRandom.current().nextDouble(
                graphHopperProp.getMinLatitude(),
                graphHopperProp.getMaxLatitude()
        );
        double longitude = ThreadLocalRandom.current().nextDouble(
                graphHopperProp.getMinLongitude(),
                graphHopperProp.getMaxLongitude()
        );

        return new GraphHopperPointDto(latitude, longitude);
    }
}

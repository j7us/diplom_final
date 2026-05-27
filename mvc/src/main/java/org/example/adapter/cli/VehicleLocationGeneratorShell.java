package org.example.adapter.cli;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.example.bootstrap.property.GraphHopperProp;
import org.example.adapter.client.RouteService;
import org.example.adapter.client.graphhopper.dto.GraphHopperPointDto;
import org.example.application.service.VehicleLocationService;
import org.example.model.VehicleLocation;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Command
@Component
@RequiredArgsConstructor
public class VehicleLocationGeneratorShell {
    private final GraphHopperProp graphHopperProp;
    private final RouteService routeService;
    private final VehicleLocationService vehicleLocationService;

    @Command(description = "Generate route points for vehicle")
    public void generateVehicleLocations(@Option UUID vehicleId) {
        if (vehicleId == null) {
            throw new RuntimeException("Идентификатор машины не задан");
        }

        GraphHopperPointDto pointFrom = randomPoint();
        GraphHopperPointDto pointTo = randomPoint();

        routeService.buildRoutePoints(pointFrom, pointTo)
                .concatMap(location -> createWithDelay(vehicleId, location))
                .then()
                .block();
    }

    private Mono<Void> createWithDelay(UUID vehicleId, VehicleLocation location) {
        return Mono.fromRunnable(() -> vehicleLocationService.create(vehicleId, location))
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.delay(Duration.ofSeconds(10)))
                .then();
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

package org.example.adapter.client;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.example.bootstrap.property.GraphHopperProp;
import org.example.adapter.client.graphhopper.dto.GraphHopperPointDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GraphHopperRouteClient {
    private final GraphHopperProp graphHopperProp;
    private final WebClient webClient;

    public Mono<String> getRoute(GraphHopperPointDto pointFrom, GraphHopperPointDto pointTo) {
        return webClient.get()
                .uri(graphHopperProp.getApiUrl(), uriBuilder -> uriBuilder
                        .queryParam("point", formatPoint(pointFrom))
                        .queryParam("point", formatPoint(pointTo))
                        .queryParam("points_encoded", false)
                        .queryParam("profile", "car")
                        .queryParam("locale", "en")
                        .queryParam("calc_points", true)
                        .queryParam("key", graphHopperProp.getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .filter(StringUtils::hasText)
                .switchIfEmpty(Mono.error(new RuntimeException("GraphHopper вернул пустой ответ")))
                .onErrorMap(WebClientException.class, ex -> new RuntimeException("Ошибка при запросе маршрута в GraphHopper", ex));
    }

    private String formatPoint(GraphHopperPointDto point) {
        return String.format(Locale.US, "%.6f,%.6f", point.getLatitude(), point.getLongitude());
    }
}

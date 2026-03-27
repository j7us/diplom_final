package org.example.client;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.example.config.GraphHopperProp;
import org.example.dto.graphhopper.GraphHopperPointDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class GraphHopperRouteClient {
    private static final String PROFILE = "car";
    private static final String LOCALE = "en";

    private final GraphHopperProp graphHopperProp;
    private final RestClient restClient;

    public String getRoute(GraphHopperPointDto pointFrom, GraphHopperPointDto pointTo) {
        try {
            String response = restClient.get()
                    .uri(graphHopperProp.getApiUrl(),uriBuilder -> uriBuilder
                            .queryParam("point", formatPoint(pointFrom))
                            .queryParam("point", formatPoint(pointTo))
                            .queryParam("points_encoded", false)
                            .queryParam("profile", PROFILE)
                            .queryParam("locale", LOCALE)
                            .queryParam("calc_points", true)
                            .queryParam("key", graphHopperProp.getApiKey())
                            .build())
                    .retrieve()
                    .body(String.class);

            if (!StringUtils.hasText(response)) {
                throw new RuntimeException("GraphHopper вернул пустой ответ");
            }

            return response;
        } catch (RestClientException ex) {
            throw new RuntimeException("Ошибка при запросе маршрута в GraphHopper", ex);
        }
    }

    private String formatPoint(GraphHopperPointDto point) {
        return String.format(Locale.US, "%.6f,%.6f", point.getLatitude(), point.getLongitude());
    }
}

package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.config.GeoapifyProp;
import org.example.entity.VehicleLocation;
import org.locationtech.jts.geom.Point;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class GeoapifyAddressClientService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final GeoapifyProp geoapifyProp;

    public List<String> getAddresses(List<VehicleLocation> locations) {
        String batchId = createBatch(locations);

        return getBatchAddresses(batchId);
    }

    private String createBatch(List<VehicleLocation> locations) {
        try {
            String response = restClient.post()
                    .uri(geoapifyProp.getBatchReverseUrl(), uriBuilder -> uriBuilder
                            .queryParam("apiKey", geoapifyProp.getApiKey())
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildBatchBody(locations))
                    .retrieve()
                    .body(String.class);

            return extractBatchId(response);
        } catch (RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<String> getBatchAddresses(String batchId) {
        Integer maxPollAttempts = geoapifyProp.getMaxPollAttempts();
        int attempt = 0;

        while (attempt < maxPollAttempts) {
            String response = requestBatchResult(batchId);
            List<String> addresses = parseAddressesIfReady(response);

            if (!CollectionUtils.isEmpty(addresses)) {
                return addresses;
            }

            attempt++;
            sleepBeforeNextPoll();
        }

        throw new RuntimeException("Исчерпано кол-во попыток");
    }

    private String requestBatchResult(String batchId) {
        try {
            return restClient.get()
                    .uri(geoapifyProp.getBatchReverseUrl(), uriBuilder -> uriBuilder
                            .queryParam("id", batchId)
                            .queryParam("apiKey", geoapifyProp.getApiKey())
                            .build())
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<String> parseAddressesIfReady(String response) {
        JsonNode rootNode = readJson(response);

        if (rootNode.isArray()) {
            return parseAddresses(rootNode);
        }

        return List.of();
    }

    private List<String> parseAddresses(JsonNode addressesNode) {
        return java.util.stream.StreamSupport.stream(addressesNode.spliterator(), false)
                .map(node -> node.path("formatted").asText())
                .toList();
    }

    private String extractBatchId(String response) {
        JsonNode rootNode = readJson(response);
        String id = rootNode.path("id").asText();

        return id;
    }

    private JsonNode readJson(String response) {
        try {
            return objectMapper.readTree(response);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<Map<String, Double>> buildBatchBody(List<VehicleLocation> locations) {
        return locations.stream()
                .map(this::mapToBatchPoint)
                .toList();
    }

    private Map<String, Double> mapToBatchPoint(VehicleLocation location) {
        Point point = location.getLocation();

        return Map.of(
                "lat", point.getY(),
                "lon", point.getX());
    }

    private void sleepBeforeNextPoll() {
        try {
            Thread.sleep(geoapifyProp.getPollDelayMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
}

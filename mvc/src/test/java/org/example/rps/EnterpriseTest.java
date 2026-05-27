package org.example.rps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


public class EnterpriseTest extends Simulation {
    private static final String BASE_URL = System.getProperty("baseUrl", "http://127.0.0.1:8080");
    private static final String USERNAME = System.getProperty("username", "manager1");
    private static final String PASSWORD = System.getProperty("password", "12345");
    private static final double START_RPS = Double.parseDouble(System.getProperty("startRps", "200"));
    private static final double TARGET_RPS = Double.parseDouble(System.getProperty("targetRps", "2000"));
    private static final int RAMP_SECONDS = Integer.parseInt(System.getProperty("rampSeconds", "60"));
    private static final int HOLD_SECONDS = Integer.parseInt(System.getProperty("holdSeconds", "120"));
    private static final AtomicReference<String> ACCESS_TOKEN = new AtomicReference<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final List<String> ENTERPRISE_IDS = List.of(
            "97363346-fb4e-43a3-a724-7c07741f187b",
            "c8a1b0d8-50cd-4d2e-b7a1-a8a8a29044c0",
            "896a2d9b-f0c3-4fba-9f22-adbf30b3fa39",
            "4e789707-7d14-4ede-9c4e-7133429d91d6",
            "a474e4b1-d5c6-4db3-8fd0-741a9ee59318"
    );

    private static final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .userAgentHeader("Performance Test")
            .shareConnections()
            .maxConnectionsPerHost(2000);

    private static final ScenarioBuilder scn = scenario("Enterprise GET (2k RPS)")
            .exec(session -> session.set(
                    "enterpriseId",
                    ENTERPRISE_IDS.get(ThreadLocalRandom.current().nextInt(ENTERPRISE_IDS.size()))
            ))
            .exec(
                    http("get-enterprise-by-id")
                            .get("/api/enterprises/#{enterpriseId}/")
                            .header("Authorization", session -> "Bearer " + ACCESS_TOKEN.get())
                            .check(status().is(200))
    );

    public EnterpriseTest() {
        setUp(
                scn.injectOpen(
                        rampUsersPerSec(START_RPS).to(TARGET_RPS).during(Duration.ofSeconds(RAMP_SECONDS)),
                        constantUsersPerSec(TARGET_RPS).during(Duration.ofSeconds(HOLD_SECONDS))
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile4().lte(2_000),
                        global().responseTime().percentile3().lte(1_000),
                        global().failedRequests().percent().lte(1.0)
                );
    }

    @Override
    public void before() {
        ACCESS_TOKEN.set(fetchAccessToken());
    }

    private static String fetchAccessToken() {
        try {
            String requestBody = """
                    {"username":"%s","password":"%s"}
                    """.formatted(USERNAME, PASSWORD);

            HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Login failed: HTTP " + response.statusCode() + " " + response.body());
            }

            JsonNode responseBody = OBJECT_MAPPER.readTree(response.body());
            String accessToken = responseBody.path("accessToken").asText();
            if (accessToken.isBlank()) {
                throw new IllegalStateException("Login response does not contain accessToken");
            }

            return accessToken;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to fetch access token from " + BASE_URL + "/login", e);
        }
    }
}

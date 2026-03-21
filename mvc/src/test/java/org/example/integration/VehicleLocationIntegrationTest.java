package org.example.integration;

import org.example.dto.vehiclelocation.VehicleLocationJsonRestDto;
import org.example.entity.VehicleLocation;
import org.example.repository.EnterpriseRepository;
import org.example.repository.ManagerRepository;
import org.example.repository.VehicleLocationRepository;
import org.example.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.utils.TestUtil.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class VehicleLocationIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgis/postgis:16-3.4-alpine").asCompatibleSubstituteFor("postgres")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.shell.interactive.enabled", () -> false);
    }

    @Autowired
    private VehicleLocationRepository vehicleLocationRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private EnterpriseRepository enterpriseRepository;
    @Autowired
    private ManagerRepository managerRepository;

    private UUID vehicleId = null;


    @BeforeEach
    void insertLocations() {
        vehicleLocationRepository.deleteAll();
        vehicleRepository.deleteAll();

        UUID enterpriseId = saveEnterprise(enterpriseRepository);
        saveManager(managerRepository, enterpriseRepository, enterpriseId);
        vehicleId = saveVehicle(vehicleRepository, enterpriseRepository, enterpriseId);
    }

    @Test
    void testGetLocations() {
        List<VehicleLocation> locations = initVehicleLocations(vehicleLocationRepository, vehicleRepository, vehicleId);

        String url = "/api/vehicle/{vehicleId}/locations?dateFrom={dateFrom}&dateTo={dateTo}&format={format}";

        Map<String, Object> params = new HashMap<>();
        params.put("vehicleId", vehicleId);
        params.put("dateFrom", "2024-01-01T10:00:00");
        params.put("dateTo", "2027-01-01T12:00:00");
        params.put("format", "json");

        String token = buildAccessToken(testRestTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<VehicleLocationJsonRestDto>> response = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {},
                params
        );

        List<UUID> resultIds = locations.stream()
                .map(VehicleLocation::getId)
                .toList();

        List<UUID> rsIds = response.getBody().stream()
                .map(VehicleLocationJsonRestDto::getId)
                .toList();

        assertThat(resultIds).contains(rsIds.toArray(UUID[]::new));
    }
}

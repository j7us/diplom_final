package org.example.bootstrap.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(value = "graphhopper")
public class GraphHopperProp {
    private String apiUrl;
    private String apiKey;
    private Double minLatitude;
    private Double maxLatitude;
    private Double minLongitude;
    private Double maxLongitude;
}

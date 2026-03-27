package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(value = "geoapify")
public class GeoapifyProp {
    private String batchReverseUrl;
    private String apiKey;
    private Integer pollDelayMillis = 10000;
    private Integer maxPollAttempts = 3;
}

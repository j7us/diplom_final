package org.example.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(value = "report")
public class ReportProp {
    private Map<String, String> translations = new HashMap<>();
}

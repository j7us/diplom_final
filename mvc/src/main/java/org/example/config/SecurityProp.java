package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "security.jwt")
@Data
public class SecurityProp {
    private String secretKey;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
}

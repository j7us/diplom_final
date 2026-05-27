package org.example.bootstrap.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Enterprise-Vehicle API",
                description = "API для управления предприятиями и их автомобилями",
                version = "1.0.0"
        )
)
public class OpenApiConfig {
}

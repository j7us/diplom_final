package org.example.adapter.controller.dto.vehicle;

import java.math.BigDecimal;
import java.util.UUID;

public record VehicleDto(
        UUID id,
        Integer milleage,
        BigDecimal price,
        String country,
        String brandName,
        UUID brandId
) {
}

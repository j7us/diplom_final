package org.example.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BrandDto(UUID id, String name, String type, Integer capacity, String drive, BigDecimal weight) {
}

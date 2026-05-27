package org.example.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private UUID id;
    private Integer milleage;
    private BigDecimal price;
    private String country;
    private Instant productionDate;
    private UUID brandId;
    private String brandName;
    private UUID enterpriseId;
    private String enterpriseTimeZone;
    private UUID activeDriverId;
    private String activeDriverName;
}

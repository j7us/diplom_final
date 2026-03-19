package org.example.dto.vehicle;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreateRestDto {
    private Integer milleage;
    private BigDecimal price;
    private String country;
    private OffsetDateTime productionDate;
    private UUID brandId;
    private UUID enterpriseId;
    private UUID activeDriverId;
}

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
public class Trip {
    private UUID id;
    private UUID vehicleId;
    private Instant dateFrom;
    private Instant dateTo;
    private BigDecimal distance;
}

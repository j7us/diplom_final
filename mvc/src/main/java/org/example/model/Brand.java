package org.example.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    private UUID id;
    private String name;
    private String type;
    private Integer capacity;
    private String drive;
    private BigDecimal weight;
}

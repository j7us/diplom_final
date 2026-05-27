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
public class Driver {
    private UUID id;
    private String name;
    private BigDecimal salary;
    private BigDecimal workExperience;
    private UUID enterpriseId;
}

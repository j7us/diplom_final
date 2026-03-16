package org.example.dto.driver;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateDto {
    private String name;
    private BigDecimal salary;
    private BigDecimal workExperience;
    private UUID enterpriseId;
}

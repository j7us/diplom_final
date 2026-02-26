package org.example.dto;

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
public class DriverRestDto {
    private UUID id;
    private String name;
    private BigDecimal salary;
    private BigDecimal workExperience;
}

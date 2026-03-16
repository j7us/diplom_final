package org.example.dto.brand;

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
public class BrandRestDto {
    private UUID id;
    private String name;
    private String type;
    private Integer capacity;
    private String drive;
    private BigDecimal weight;
}

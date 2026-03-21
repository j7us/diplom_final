package org.example.dto.enterprise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseImport {
    private String name;
    private String country;
    private Integer productionCapacity;
    private String timeZone;
}

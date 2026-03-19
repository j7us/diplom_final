package org.example.dto;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseRestDto {
    private UUID id;
    private String name;
    private String country;
    private Integer productionCapacity;
    private String timeZone;
    private List<UUID> driverIds;
    private List<UUID> vehicleIds;
}

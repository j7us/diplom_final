package org.example.model;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enterprise {
    private UUID id;
    private String name;
    private String country;
    private Integer productionCapacity;
    private String timeZone;
    private List<UUID> driverIds;
    private List<UUID> vehicleIds;
    private List<UUID> managerIds;
}

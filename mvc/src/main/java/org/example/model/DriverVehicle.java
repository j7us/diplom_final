package org.example.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverVehicle {
    private UUID id;
    private UUID driverId;
    private UUID vehicleId;
    private Boolean active;
}

package org.example.dto.driver;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverVehicleCreateDto {
    private UUID driverId;
    private UUID vehicleId;
    private Boolean active;
}

package org.example.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocation {
    private UUID id;
    private GeoPoint location;
    private LocalDateTime date;
    private UUID vehicleId;
}

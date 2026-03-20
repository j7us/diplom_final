package org.example.dto.vehiclelocation;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocationGeoJsonRestDto {
    private UUID id;
    private LocalDateTime date;
    private Point location;
}

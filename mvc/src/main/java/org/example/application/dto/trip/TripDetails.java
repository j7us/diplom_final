package org.example.application.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Trip;
import org.example.model.VehicleLocation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripDetails {
    private Trip trip;
    private VehicleLocation startLocation;
    private VehicleLocation endLocation;
    private String startAddress;
    private String endAddress;
}
